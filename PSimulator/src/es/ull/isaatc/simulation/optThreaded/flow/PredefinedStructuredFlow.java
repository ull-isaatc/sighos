/**
 * 
 */
package es.ull.isaatc.simulation.optThreaded.flow;

import es.ull.isaatc.simulation.optThreaded.Simulation;
import es.ull.isaatc.simulation.optThreaded.WorkThread;


/**
 * An structured flow with predefined both entry and exit points.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class PredefinedStructuredFlow extends StructuredFlow implements es.ull.isaatc.simulation.common.flow.PredefinedStructuredFlow {

	/**
	 * Creates a new structured flow with predefined entry and exit points.
	 * @param simul Simulation this flow belongs to 
	 */
	public PredefinedStructuredFlow(Simulation simul) {
		super(simul);
	}
	
	/**
	 * Adds a new branch starting in <code>initialBranch</code> and finishing in <code>finalBranch</code>.
	 * The <code>initialFlow</code> is linked to the <code>initialBranch</code> whereas
	 * the <code>final Branch</code> is linked to the <code>finalFlow</code> 
	 * @param initialBranch First step of the internal branch
	 * @param finalBranch Last step of the internal branch
	 */
	public void addBranch(es.ull.isaatc.simulation.common.flow.InitializerFlow initialBranch, es.ull.isaatc.simulation.common.flow.FinalizerFlow finalBranch) {
		initialBranch.setRecursiveStructureLink(this);
		initialFlow.link(initialBranch);
		finalBranch.link(finalFlow);		
	}
	
	/**
	 * Adds a new branch consisting of a unique flow. The <code>branch</code> has the
	 * <code>initialFlow</code> as predecessor and the <code>finalFlow</code> as successor. 
	 * @param branch A unique flow defining an internal branch
	 */
	public void addBranch(es.ull.isaatc.simulation.common.flow.TaskFlow branch) {
		addBranch(branch, branch);		
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement()))
					initialFlow.request(wThread.getInstanceDescendantWorkThread(initialFlow));
				else {
					wThread.setExecutable(false, this);
					next(wThread);				
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.TaskFlow#finish(es.ull.isaatc.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		afterFinalize(wThread.getElement());
		next(wThread);
	}
}