/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import java.util.TreeSet;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;


/**
 * An structured flow with predefined both entry and exit points.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class PredefinedStructuredFlow extends StructuredFlow implements es.ull.iis.simulation.core.flow.PredefinedStructuredFlow {

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
	public void addBranch(es.ull.iis.simulation.core.flow.InitializerFlow initialBranch, es.ull.iis.simulation.core.flow.FinalizerFlow finalBranch) {
		final TreeSet<es.ull.iis.simulation.core.flow.Flow> visited = new TreeSet<es.ull.iis.simulation.core.flow.Flow>();
		initialBranch.setRecursiveStructureLink(this, visited);
		initialFlow.link(initialBranch);
		finalBranch.link(finalFlow);		
	}
	
	/**
	 * Adds a new branch consisting of a unique flow. The <code>branch</code> has the
	 * <code>initialFlow</code> as predecessor and the <code>finalFlow</code> as successor. 
	 * @param branch A unique flow defining an internal branch
	 */
	public void addBranch(es.ull.iis.simulation.core.flow.TaskFlow branch) {
		addBranch(branch, branch);		
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#request(es.ull.iis.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement()))
					wThread.getInstanceDescendantWorkThread().requestFlow(initialFlow);
				else {
					wThread.cancel(this);
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
	 * @see es.ull.iis.simulation.TaskFlow#finish(es.ull.iis.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		afterFinalize(wThread.getElement());
		next(wThread);
	}
}
