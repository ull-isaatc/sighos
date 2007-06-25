/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.SimulationState;

/**
 * A simulation with standard AM configuration which creates a single LP for the
 * whole simulation.
 * @see StandardAMSimulation
 * @author Iván Castilla Rodríguez
 */
public abstract class StandAloneLPSimulation extends StandardAMSimulation {

	/**
	 * Empty constructor for compatibility purposes
	 */
	public StandAloneLPSimulation() {		
	}

	/**
	 * Creates a simulation with standard AM configuration which instances a single LP
	 * @param id
	 *            This simulation's identifier
	 * @param description 
	 *            This simulation's description.
	 * @param startTs
	 *            Timestamp of simulation's start
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public StandAloneLPSimulation(int id, String description, double startTs, double endTs) {
		super(id, description, startTs, endTs);
	}

	/**
	 * Creates a simulation with standard AM configuration which instances a single LP
	 * @param id
	 *            This simulation's identifier
	 * @param description 
	 *            This simulation's description.
	 * @param previousState
	 *            A previous stored state
	 * @param endTs
	 *            Timestamp of simulation's end
	 */
	public StandAloneLPSimulation(int id, String description, SimulationState previousState, double endTs) {
		super(id, description, previousState, endTs);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[1];
		logicalProcessList[0] = new LogicalProcess(this, startTs, endTs);
		for (ActivityManager am : activityManagerList)
			am.setLp(logicalProcessList[0]);
	}
}
