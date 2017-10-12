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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

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
	/** Starting position of each crane */
	private final int[] startingPositions;
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
	/** For each crane, a collection of trios <previous_container, bay, dependent_container> that indicate that after performing task "previous_container" the crane has
	 * to move to "bay" and wait until the task for "dependent_container" is finished. Trios must be in ascending order by "previous_container". */
	private final ArrayList<ArrayDeque<int []>> waits;
	private final ArrayList<TreeSet<Integer>> dependanceTasks;
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
	public StowagePlan(QCSPSolution originalSolution, Vessel vessel, int nCranes, int safetyDistance, long objectiveValue, double overlap, boolean leftToRight, 
			int []startingPositions, TreeMap<Integer, Integer>[] orderedTasks, ArrayList<ArrayDeque<int []>> waits) {
		schedule = (ArrayList<Integer>[]) new ArrayList<?>[nCranes];
		for (int i = 0; i < nCranes; i++)
			schedule[i] = new ArrayList<Integer>();
		this.startingPositions = startingPositions;
		this.vessel = vessel;
		this.nCranes = nCranes;
		this.safetyDistance = safetyDistance;
		this.objectiveValue = objectiveValue;
		this.overlap = overlap;
		this.craneDoTask = new int[vessel.getNContainers()];
		this.leftToRight = leftToRight;
		nContainers = 0;
		this.originalSolution = originalSolution;
		this.waits = waits;
		this.dependanceTasks = new ArrayList<>(nCranes);
		for (int qc = 0; qc < nCranes; qc++) {
			addAll(qc, orderedTasks[qc].values());
			dependanceTasks.add(new TreeSet<>());
		}
		// Creates a list of tasks that a crane has to perform and that creates a dependency in another crane
		for (int qc = 0; qc < nCranes; qc++) {
			for (int[] dummyWait : waits.get(qc)) {
				// FIXME: terminar
				final int depTask = dummyWait[2];
				final int depCrane = getCraneDoTask(depTask);
				dependanceTasks.get(depCrane).add(dummyWait[2]);
			}
		}
	}

	/**
	 * Adds a set of containers to the list of tasks of a crane
	 * @param craneId Crane identifier
	 * @param containers List of containers
	 */
    private void addAll(int craneId, Collection<Integer> containers) {
        schedule[craneId].addAll(containers);
        nContainers += containers.size();
        for (int contId : containers)
        	craneDoTask[contId] = craneId;
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
	 * Returns the initial position (bay) of a specified crane
	 * @param craneId Crane identifier
	 * @return the initial position (bay) of a specified crane
	 */
	public int getStartingPosition(int craneId) {
		return startingPositions[craneId];
	}
	
	/**
	 * @return the dependanceTasks
	 */
	public ArrayList<TreeSet<Integer>> getDependanceTasks() {
		return dependanceTasks;
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
	 * @return the originalSolution
	 */
	public QCSPSolution getOriginalSolution() {
		return originalSolution;
	}

	/**
	 * @return the dependenciesAtStart
	 */
	public ArrayDeque<int[]> getWaits(int craneId) {
		return waits.get(craneId);
	}

	public int[] getNextWaitIfNeeded(int craneId, int containerId) {
		if (waits.get(craneId).size() == 0)
			return null;
		if (waits.get(craneId).getFirst()[0] == containerId)
			return waits.get(craneId).pop();
		return null;
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
			str.append("Crane " + i + " (INIT:" + startingPositions[i] + "):");
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
