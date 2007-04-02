/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * A simulation with standard AM configuration which creates a single LP for the
 * whole simulation.
 * @see StandardAMSimulation
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class StandAloneLPSimulation extends StandardAMSimulation {

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
