/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.simulation.state.SimulationState;

/**
 * A simulation with standard AM configuration which creates a single LP for the
 * whole simulation.
 * @see StandardAMSimulation
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class StandAloneLPSimulation extends StandardAMSimulation {

	/**
	 * Empty constructor for compatibility purposes
	 */
	public StandAloneLPSimulation() {		
	}

	/**
	 * Creates a new instance of Simulation
	 * 
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public StandAloneLPSimulation(int id, String description, SimulationTimeUnit unit) {
		super(id, description, unit);
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
	public StandAloneLPSimulation(int id, String description, SimulationTimeUnit unit, SimulationTime startTs, SimulationTime endTs) {
		super(id, description, unit, startTs, endTs);
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
	public StandAloneLPSimulation(int id, String description, SimulationTimeUnit unit, SimulationState previousState, SimulationTime endTs) {
		super(id, description, unit, previousState, endTs);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[1];
		logicalProcessList[0] = new LogicalProcess(this, internalStartTs, internalEndTs, nThreads);
		for (ActivityManager am : activityManagerList)
			am.setLp(logicalProcessList[0]);
	}
}