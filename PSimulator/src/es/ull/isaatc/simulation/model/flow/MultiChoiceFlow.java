package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow implements es.ull.isaatc.simulation.common.flow.MultiChoiceFlow {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 * @param model Model this flow belongs to
	 */
	public MultiChoiceFlow(Model model) {
		super(model);
	}
}
