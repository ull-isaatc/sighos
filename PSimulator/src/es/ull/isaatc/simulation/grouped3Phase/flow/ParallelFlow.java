/**
 * 
 */
package es.ull.isaatc.simulation.grouped3Phase.flow;

import es.ull.isaatc.simulation.grouped3Phase.Simulation;
import es.ull.isaatc.simulation.grouped3Phase.WorkThread;


/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iv�n Castilla Rodr�guez
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
				wThread.getInstanceSubsequentWorkThread(wThread.isExecutable(), this, wThread.getToken()).requestFlow(succ);
        wThread.notifyEnd();
	}
}
