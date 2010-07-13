/**
 * 
 */
package es.ull.isaatc.simulation.sequential3Phase;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SingleLogicalProcessCreator extends LogicalProcessCreator {
	private final boolean optimized;
	
	/**
	 * @param simul
	 */
	public SingleLogicalProcessCreator(Simulation simul) {
		this(simul, false);
	}

	public SingleLogicalProcessCreator(Simulation simul, boolean opt) {
		super(simul);
		this.optimized = opt;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.LogicalProcessCreator#createLogicalProcesses()
	 */
	// FIXME: Hay que hacer este método más encapsulado
	@Override
	protected void createLogicalProcesses() {
		simul.logicalProcessList = new LogicalProcess[1];
		if (optimized)
			simul.logicalProcessList[0] = new OptLogicalProcess (simul, simul.getInternalStartTs(), simul.getInternalEndTs());
		else
			simul.logicalProcessList[0] = new StdLogicalProcess (simul, simul.getInternalStartTs(), simul.getInternalEndTs());
		for (ActivityManager am : simul.getActivityManagerList())
			am.setLp(simul.logicalProcessList[0]);
	}

}
