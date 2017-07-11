/**
 * 
 */
package es.ull.iis.simulation.port.sea2yard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * A stowage plan that defines how a set of cranes will unload the containers from a vessel. Defines both the crane and the order 
 * of the unload operations.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class StowagePlan implements Serializable {
	/** Constant for serializing this class */
	private static final long serialVersionUID = -3247224781219705052L;
	/** An ordered list of containers per each crane */
	private final ArrayList<Integer>[] schedule;
	/** Which crane unloads each container */
	private final int[] craneDoTask;
	/** Initial position of each crane */
	private final int[] initPosition;
	/** The corresponding vessel */
	private final Vessel vessel;
	/** Number of containers to unload */ 
	private int nContainers;
	/** Number of available quay cranes */
	private final int nCranes;
	/** Safety distance, i.e., how far (in bays) must be a pair of cranes in order to operate or move */
	private final int safetyDistance;
	/** The theoretical optimum time to fulfill the stowage plan, precomputed by the QCSP solver  */
	private final long objectiveValue;

	/**
	 * Creates a new stowage plan
	 * @param vessel Associated vessel
	 * @param nCranes Number of available quay cranes
	 * @param safetyDistance How far (in bays) must be a pair of cranes in order to operate or move
	 * @param objectiveValue The theoretical optimum time to fulfill the stowage plan
	 */
	@SuppressWarnings("unchecked")
	public StowagePlan(Vessel vessel, int nCranes, int safetyDistance, long objectiveValue) {
		schedule = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
		for (int i = 0; i < nCranes; i++)
			schedule[i] = new ArrayList<Integer>();
		initPosition = new int[nCranes];
		this.vessel = vessel;
		this.nCranes = nCranes;
		this.safetyDistance = safetyDistance;
		this.objectiveValue = objectiveValue;
		this.craneDoTask = new int[vessel.getNContainers()];
		nContainers = 0;
	}

	/**
	 * Adds a set of containers to the list of tasks of a crane
	 * @param craneId Crane identifier
	 * @param containers List of containers
	 */
    public void addAll(int craneId, ArrayList<Integer> containers) {
        schedule[craneId].addAll(containers);
        nContainers += containers.size();
        for (int contId : containers)
        	craneDoTask[contId] = craneId;
    }

	/**
	 * Adds a set of containers to the list of tasks of a crane
	 * @param craneId Crane identifier
	 * @param containers Array of containers
	 */
	public void addAll(int craneId, int[] containers) {
		nContainers += containers.length;
		for (int contId : containers) {
			schedule[craneId].add(contId);
			craneDoTask[contId] = craneId;
		}
	}
	
	/**
	 * Returns the schedule for a specific crane
	 * @param craneId Crane identifier
	 * @return the schedule for a specific crane
	 */
	public ArrayList<Integer> getSchedule(int craneId) {
		return schedule[craneId];
	}
	
	/**
	 * Sets the cranes' initial positions (expressed as a bay)
	 * @param craneId Crane identifier
	 * @param initPos The bay where a crane starts
	 */
	public void setInitialPosition(int craneId, int initPos) {
		initPosition[craneId] = initPos;
	}
	
	/**
	 * Returns the initial position (bay) of a specified crane
	 * @param craneId Crane identifier
	 * @return the initial position (bay) of a specified crane
	 */
	public int getInitialPosition(int craneId) {
		return initPosition[craneId];
	}
	
	/**
	 * Returns the number of containers to unload
	 * @return the number of containers to unload
	 */
	public int getNContainers() {
		return nContainers;
	}

	/**
	 * Returns the number of avaiable quay cranes
	 * @return the number of avaiable quay cranes
	 */
	public int getNCranes() {
		return nCranes;
	}

	/**
	 * Returns how far (in bays) must be a pair of cranes in order to operate or move
	 * @return how far (in bays) must be a pair of cranes in order to operate or move
	 */
	public int getSafetyDistance() {
		return safetyDistance;
	}

	/**
	 * Returns the theoretical optimum time to fulfill the stowage plan
	 * @return the theoretical optimum time to fulfill the stowage plan
	 */
	public long getObjectiveValue() {
		return objectiveValue;
	}

	/**
	 * Returns the structure of the vessel
	 * @return the structure of the vessel
	 */
	public Vessel getVessel() {
		return vessel;
	}

	/**
	 * Returns the crane that unloads the specified container
	 * @param containerId Container identifier
	 * @return The identifier of the crane that unloads a specified container
	 */
	public int getCraneDoTask(int containerId) {
		return craneDoTask[containerId];
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < schedule.length; i++) {
			str.append("Crane " + i + " (INIT:" + initPosition[i] + "):");
			for (int containerId : schedule[i]) {
				str.append("\t" + containerId);
			}
			str.append("\n");
		}
		return str.toString();
	}
	
	/**
	 * Saves this stowage plan to a file
	 * @param fileName File name to save this stowage plan
	 */
	public void saveToFile(String fileName) {
		ObjectOutput output = null;
		try {
			output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
			output.writeObject(this);
		}  
		catch(IOException ex) {
			System.err.println("Error creating file for Stowage plan. ");
			ex.printStackTrace();
		}
		finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads a stowage plan from a file 
	 * @param fileName File name that stores the stowage plan
	 * @return A new stowage plan as stored in a file
	 */
	public static StowagePlan loadFromFile(String fileName) {
		ObjectInput input = null;
		StowagePlan plan = null;
		try {
			input = new ObjectInputStream (new BufferedInputStream(new FileInputStream(fileName)));
			plan = (StowagePlan)input.readObject();
		}
		catch(ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch(IOException ex){
			System.err.println("Could not open file " + fileName + " to load stowage plan");
			ex.printStackTrace();
		}
		finally{
			try {
				if (input != null) {
					input.close();
					return plan;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
