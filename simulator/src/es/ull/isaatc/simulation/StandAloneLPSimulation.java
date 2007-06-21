/**
 * 
 */
package es.ull.isaatc.simulation;

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
	 * @param description The simulation's description
	 */
	public StandAloneLPSimulation(String description) {
		super(description);
	}

	@Override
	protected void createLogicalProcesses() {
		logicalProcessList = new LogicalProcess[1];
		logicalProcessList[0] = new LogicalProcess(this, startTs, endTs);
		for (ActivityManager am : activityManagerList)
			am.setLp(logicalProcessList[0]);
	}
}
