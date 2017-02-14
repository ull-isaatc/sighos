package es.ull.iis.simulation.model.flow;

/**
 * A conditional flow which allows all outgoing branches which meet their condition to be activated.
 * Successors are evaluated in order. The rest of branches produce a false work thread.<p>   
 * Meets the Multi-Choice pattern (WFP6). 
 * Successors are evaluated in order.
 * @author ycallero
 */
public class MultiChoiceFlow extends ConditionalFlow {
	
	/**
	 * Creates a new MultiChoiceFlow.
	 */
	public MultiChoiceFlow() {
		super();
	}

}
