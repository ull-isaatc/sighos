/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

import es.ull.isaatc.simulation.model.Time;
import es.ull.isaatc.simulation.model.TimeUnit;


/**
 * A simulation with standard AM configuration which creates a LP per AM.
 * This class is not finished and should not be used.
 * @see StandardAMSimulation
 * @author Iván Castilla Rodríguez
 */
public abstract class SimpleLPSimulation extends StandardAMSimulation {

	/**
	 * Creates a simulation which creates a LP per AM.
	 * 
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public SimpleLPSimulation(int id, String description, TimeUnit unit) {
		super(id, description, unit);
	}
	
	/**
	 * Creates a simulation which creates a LP per AM.
	 * @param id
	 *            This simulation's identifier
	 * @param description 
	 *            This simulation's description.
	 * @param startTs
	 *            Timestamp of simulation's start
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public SimpleLPSimulation(int id, String description, TimeUnit unit, Time startTs, Time endTs) {
		super(id, description, unit, startTs, endTs);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[activityManagerList.size() + 1];
		for (int i = 0; i < activityManagerList.size(); i++)
			logicalProcessList[i] = new LogicalProcess(this, internalStartTs, internalEndTs);
		// The last logical process serves as a default one
		logicalProcessList[activityManagerList.size()] = new LogicalProcess(this, internalStartTs, internalEndTs);
	}

}
