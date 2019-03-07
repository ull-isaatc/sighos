package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.Simulation;


/**
 * A {@link ConditionalFlow} which allows only one of the outgoing branches to be activated.
 * Successors are evaluated in order. When one of the outgoing branches meets its
 * associated condition, a new true element instance continues. The rest of branches produce
 * a false element instance.<p>   
 * Meets the Exclusive Choice pattern (WFP4). 
 * @author Yeray Callero
 *
 */
public class ExclusiveChoiceFlow extends ConditionalFlow {

	/**
	 * Creates a new ExclusiveChoiceFlow.
	 * @param model The simulation model this flow belongs to
	 */
	public ExclusiveChoiceFlow(final Simulation model) {
		super(model);
	}

	@Override
	public void next(final ElementInstance ei) {
		super.next(ei);
		boolean res = false;
		if (ei.isExecutable())
			for (int i = 0; i < successorList.size(); i++) {
				if (!res) {
					// Check the succesor's conditions.
					res = conditionList.get(i).check(ei);
					ei.getElement().addRequestEvent(successorList.get(i), ei.getSubsequentElementInstance(res, this, ei.getToken()));
				}
				// As soon as there is one true outgoing branch, the rest of branches are false
				else
					ei.getElement().addRequestEvent(successorList.get(i), ei.getSubsequentElementInstance(false, this, ei.getToken()));
			}
		else
			for (int i = 0; i < successorList.size(); i++)
				ei.getElement().addRequestEvent(successorList.get(i), ei.getSubsequentElementInstance(false, this, ei.getToken()));
		ei.notifyEnd();
	}
}
