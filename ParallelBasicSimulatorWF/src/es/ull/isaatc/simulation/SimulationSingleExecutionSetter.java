/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.SingleThreadPool;
import es.ull.isaatc.util.ThreadPool;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationSingleExecutionSetter extends SimulationExecutorSetter {

	/**
	 * @param simul
	 */
	public SimulationSingleExecutionSetter(Simulation simul) {
		super(simul);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.SimulationExecutorSetter#setExecutors()
	 */
	@Override
	protected void setExecutors() {
		for (ActivityManager am : simul.getActivityManagerList()) {
			ThreadPool<BasicElement.DiscreteEvent> amTp = new SingleThreadPool<BasicElement.DiscreteEvent>(); 
			executorList.add(amTp);
			am.setExecutor(amTp);
		}
		ThreadPool<BasicElement.DiscreteEvent> lpTp = new SingleThreadPool<BasicElement.DiscreteEvent>();
		executorList.add(lpTp);
		for (LogicalProcess lp : simul.getLogicalProcessList())
			lp.setExecutor(lpTp);
	}

}
