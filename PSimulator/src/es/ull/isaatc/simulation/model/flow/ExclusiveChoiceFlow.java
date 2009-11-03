package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;


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
public class ExclusiveChoiceFlow extends ConditionalFlow implements es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow {

	/**
	 * Creates a new ExclusiveChoiceFlow.
	 * @param model Model this flow belongs to
	 */
	public ExclusiveChoiceFlow(Model model) {
		super(model);
	}
}
