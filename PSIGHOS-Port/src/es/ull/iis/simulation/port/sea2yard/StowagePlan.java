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
import java.util.Collection;

import com.kaizten.simulation.ports.qcsp.data.QCSPSolution;

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
	/** An ordered list of tasks per each crane */
	private final ArrayList<Integer>[] schedule;
	/** Which crane performs each task */
	private final int[] craneDoTask;
	/** Initial position of each crane */
	private final int[] initPosition;
	/** The corresponding vessel */
	private final Vessel vessel;
	/** Number of tasks */ 
	private int nContainers;
	/** Number of available quay cranes */
	private final int nCranes;
	/** Safety distance, i.e., how far (in bays) must be a pair of cranes in order to operate or move */
	private final int safetyDistance;
	/** The theoretical optimum time to fulfill the stowage plan, precomputed by the QCSP solver  */
	private final long objectiveValue;
	/** A measure of the overlapping degree of the solution, precomputed by the QCSP solver */
	private final double overlap;
	private final double lowerBoundOverlapping;
	private final double upperBoundOverlapping;
	/** For each crane, a pair of <position, task>, where position represents a bay where the crane has to move at start to avoid a conflict,
	 * and task is the last task another crane has to finish before this crane can start moving again */
	private final int[][] dependenciesAtStart;
	/** True if the solution goes left to right; false otherwise */
	private final boolean leftToRight;
	private final QCSPSolution originalSolution;

	/**
	 * Creates a new stowage plan
	 * @param vessel Associated vessel
	 * @param nCranes Number of available quay cranes
	 * @param safetyDistance How far (in bays) must be a pair of cranes in order to operate or move
	 * @param objectiveValue The theoretical optimum time to fulfill the stowage plan
	 */
	@SuppressWarnings("unchecked")
	public StowagePlan(QCSPSolution originalSolution, Vessel vessel, int nCranes, int safetyDistance, long objectiveValue, double overlap, double lowerBoundOverlapping, double upperBoundOverlapping, boolean leftToRight, int[][] dependenciesAtStart) {
		schedule = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
		for (int i = 0; i < nCranes; i++)
			schedule[i] = new ArrayList<Integer>();
		initPosition = new int[nCranes];
		this.vessel = vessel;
		this.nCranes = nCranes;
		this.safetyDistance = safetyDistance;
		this.objectiveValue = objectiveValue;
		this.overlap = overlap;
		this.lowerBoundOverlapping = lowerBoundOverlapping;
		this.upperBoundOverlapping = upperBoundOverlapping;
		this.dependenciesAtStart = dependenciesAtStart;
		this.craneDoTask = new int[vessel.getNContainers()];
		this.leftToRight = leftToRight;
		nContainers = 0;
		this.originalSolution = originalSolution;
	}

	/**
	 * Adds a set of containers to the list of tasks of a crane
	 * @param craneId Crane identifier
	 * @param containers List of containers
	 */
    public void addAll(int craneId, Collection<Integer> containers) {
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
	public int getNTasks() {
		return nContainers;
	}

	/**
	 * Returns the optimum start time for task <code>taskId</code> according to the QCSP solver
	 * @param taskId Identifier of the task
	 * @return The optimum start time for task <code>taskId</code> according to the QCSP solver
	 */
	public long getOptStartTime(int taskId) {
		return vessel.getContainerOptStartTime(taskId);
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
	 * Returns a measure of the overlapping degree of the solution
	 * @return A measure of the overlapping degree of the solution
	 */
	public double getOverlap() {
		return overlap;
	}

	/**
	 * @return the lowerBoundOverlapping
	 */
	public double getLowerBoundOverlapping() {
		return lowerBoundOverlapping;
	}

	/**
	 * @return the upperBoundOverlapping
	 */
	public double getUpperBoundOverlapping() {
		return upperBoundOverlapping;
	}

	/**
	 * @return the dependenciesAtStart
	 */
	public int[] getDependenciesAtStart(int craneId) {
		return dependenciesAtStart[craneId];
	}

	/**
	 * @return the originalSolution
	 */
	public QCSPSolution getOriginalSolution() {
		return originalSolution;
	}

	/**
	 * A crane creates a conflict at start when its first task is most to the left than its left-neighbor's first task.  
	 * @param craneId Crane identifier
	 * @return True if the crane creates a conflict at start
	 */
	public boolean createsConflictAtStart(int craneId) {
		if (leftToRight && (craneId > 0)) {
			if (dependenciesAtStart[craneId - 1][1] != -1) {
				return true;
			}
		}
		else if (!leftToRight && (craneId < nCranes - 1)) {
			if (dependenciesAtStart[craneId + 1][1] != -1) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns true if the solution goes left to right; false otherwise
	 * @return true if the solution goes left to right; false otherwise
	 */
	public boolean isLeftToRight() {
		return leftToRight;
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
