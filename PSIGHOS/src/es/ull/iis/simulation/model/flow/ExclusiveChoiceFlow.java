package es.ull.iis.simulation.model.flow;

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
	public ExclusiveChoiceFlow() {
		super();
	}

}
