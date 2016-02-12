/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;
import es.ull.iis.simulation.parallel.WorkThread;


/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow implements es.ull.iis.simulation.core.flow.ParallelFlow {

	/**
	 * Creates a new ParallelFlow
	 * @param simul Simulation this flow belongs to
	 */
	public ParallelFlow(Simulation simul) {
		super(simul);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicFlow#next(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		if (successorList.size() > 0)
			for(Flow succ : successorList)
				wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()).requestFlow(succ);
        wThread.notifyEnd();
	}
}
