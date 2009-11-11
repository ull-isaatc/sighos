/**
 * 
 */
package es.ull.isaatc.simulation.threaded;


/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class SingleLogicalProcessCreator extends LogicalProcessCreator {
	
	/**
	 * @param simul
	 */
	public SingleLogicalProcessCreator(Simulation simul) {
		super(simul);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcessCreator#createLogicalProcesses()
	 */
	// FIXME: Hay que hacer este m�todo m�s encapsulado
	@Override
	protected void createLogicalProcesses() {
		simul.logicalProcessList = new LogicalProcess[1];
		simul.logicalProcessList[0] = new LogicalProcess (simul, simul.getInternalStartTs(), simul.getInternalEndTs(), simul.getNThreads());
		for (ActivityManager am : simul.getActivityManagerList())
			am.setLp(simul.logicalProcessList[0]);
	}

}
