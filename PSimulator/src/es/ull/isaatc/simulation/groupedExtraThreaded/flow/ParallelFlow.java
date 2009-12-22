/**
 * 
 */
package es.ull.isaatc.simulation.groupedExtraThreaded.flow;

import es.ull.isaatc.simulation.groupedExtraThreaded.Simulation;
import es.ull.isaatc.simulation.groupedExtraThreaded.WorkThread;


/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow implements es.ull.isaatc.simulation.common.flow.ParallelFlow {

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
				wThread.getElement().addRequestEvent(succ, wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()));
        wThread.notifyEnd();
	}
}
