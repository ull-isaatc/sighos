/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;

/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow {

	/**
	 * Creates a new ParallelFlow
	 */
	public ParallelFlow(Model model) {
		super(model);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	public void next(FlowExecutor wThread) {
		super.next(wThread);
		if (successorList.size() > 0)
			for(Flow succ : successorList)
				wThread.getElement().addRequestEvent(succ, wThread.getInstanceSubsequentFlowExecutor(wThread.isExecutable(), this, wThread.getToken()));
        wThread.notifyEnd();
	}
}
