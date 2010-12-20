package es.ull.isaatc.simulation.parallel.flow;

import es.ull.isaatc.simulation.parallel.Simulation;
import es.ull.isaatc.simulation.parallel.WorkThread;


/**
 * A conditional flow which allows only one of the outgoing branches to be activated.
 * Successors are evaluated in order. When one of the outgoing branches meets its
 * associated condition, a new true work thread continues. The rest of branches produce
 * a false work thread.<p>   
 * Meets the Exclusive Choice pattern (WFP4). 
 * Successors are evaluated in order.
 * @author ycallero
 *
 */
public class ExclusiveChoiceFlow extends ConditionalFlow implements es.ull.isaatc.simulation.flow.ExclusiveChoiceFlow {

	/**
	 * Creates a new ExclusiveChoiceFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public ExclusiveChoiceFlow(Simulation simul) {
		super(simul);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.BasicFlow#next(es.ull.isaatc.simulation.WorkThread)
	 */
	@Override
	public void next(WorkThread wThread) {
		super.next(wThread);
		boolean res = false;
		if (wThread.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				if (!res) {
					// Check the succesor's conditions.
					res = conditionList.get(i).check(wThread.getElement());
					wThread.getInstanceSubsequentWorkThread(res, this, wThread.getToken()).requestFlow(successorList.get(i));
				}
				// As soon as there is one true outgoing branch, the rest of branches are false
				else
					wThread.getInstanceSubsequentWorkThread(false, this, wThread.getToken()).requestFlow(successorList.get(i));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				wThread.getInstanceSubsequentWorkThread(false, this, wThread.getToken()).requestFlow(successorList.get(i));
		wThread.notifyEnd();
	}
}
