/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleLogicalProcessCreator extends LogicalProcessCreator {

	/**
	 * @param simul
	 */
	public SingleLogicalProcessCreator(Simulation simul) {
		super(simul);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcessCreator#createLogicalProcesses()
	 */
	// FIXME: Hay que hacer este método más encapsulado
	@Override
	protected void createLogicalProcesses() {
		simul.logicalProcessList = new LogicalProcess[1];
		simul.logicalProcessList[0] = new LogicalProcess (simul, simul.getInternalStartTs(), simul.getInternalEndTs());
		for (ActivityManager am : simul.getActivityManagerList())
			am.setLp(simul.logicalProcessList[0]);
	}

}
