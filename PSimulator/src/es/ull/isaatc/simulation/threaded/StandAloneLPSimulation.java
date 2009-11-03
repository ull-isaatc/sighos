/**
 * 
 */
package es.ull.isaatc.simulation.threaded;

import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;


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
	 * Creates a new instance of Simulation
	 * 
	 * @param id This simulation's identifier
	 * @param description A short text describing this simulation.
	 */
	public StandAloneLPSimulation(int id, String description, TimeUnit unit) {
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
	public StandAloneLPSimulation(int id, String description, TimeUnit unit, Time startTs, Time endTs) {
		super(id, description, unit, startTs, endTs);
	}

	/**
	 * Creates a simulation with standard AM configuration which instances a single LP
	 * @param id
	 *            This simulation's identifier
	 * @param description 
	 *            This simulation's description.
	 * @param startTs
	 *            Simulation's start timestamp expresed in Simulation Time Units
	 * @param endTs
	 *            Simulation's end timestamp expresed in Simulation Time Units
	 */
	public StandAloneLPSimulation(int id, String description, TimeUnit unit, double startTs, double endTs) {
		super(id, description, unit, startTs, endTs);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[1];
		logicalProcessList[0] = new LogicalProcess (this, internalStartTs, internalEndTs, nThreads);
		for (ActivityManager am : activityManagerList)
			am.setLp(logicalProcessList[0]);
	}
}
