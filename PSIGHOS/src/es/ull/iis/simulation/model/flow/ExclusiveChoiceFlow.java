package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.FlowExecutor;
import es.ull.iis.simulation.model.Model;


/**
 * A {@link ConditionalFlow} which allows only one of the outgoing branches to be activated.
 * Successors are evaluated in order. When one of the outgoing branches meets its
 * associated condition, a new true work thread continues. The rest of branches produce
 * a false work thread.<p>   
 * Meets the Exclusive Choice pattern (WFP4). 
 * @author Yeray Callero
 *
 */
public class ExclusiveChoiceFlow extends ConditionalFlow {

	/**
	 * Creates a new ExclusiveChoiceFlow.
	 */
	public ExclusiveChoiceFlow(Model model) {
		super(model);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.BasicFlow#next(es.ull.iis.simulation.FlowExecutor)
	 */
	@Override
	public void next(FlowExecutor wThread) {
		super.next(wThread);
		boolean res = false;
		if (wThread.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				if (!res) {
					// Check the succesor's conditions.
					res = conditionList.get(i).check(wThread);
					wThread.getElement().addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentFlowExecutor(res, this, wThread.getToken()));
				}
				// As soon as there is one true outgoing branch, the rest of branches are false
				else
					wThread.getElement().addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentFlowExecutor(false, this, wThread.getToken()));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				wThread.getElement().addRequestEvent(successorList.get(i), wThread.getInstanceSubsequentFlowExecutor(false, this, wThread.getToken()));
		wThread.notifyEnd();
	}
}