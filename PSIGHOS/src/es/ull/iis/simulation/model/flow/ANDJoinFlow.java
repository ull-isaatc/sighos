/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Model;

/**
 * A merge flow which allows only one of the incoming branches to pass. Which one
 * passes depends on the <code>acceptValue</code>.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class ANDJoinFlow extends MergeFlow {
	/** The number of branches which have to arrive to pass the control thread */
	protected int acceptValue;

	/**
	 * Creates a new AND flow.
	 */
	public ANDJoinFlow(Model model) {
		this(model, true, 0);
	}
	
	/**
	 * Creates a new AND flow
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Model model, int acceptValue) {
		this(model, true, acceptValue);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 */
	public ANDJoinFlow(Model model, boolean safe) {
		this(model, safe, 0);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param safe True for safe context; false in other case
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Model model, boolean safe, int acceptValue) {
		super(model, safe);
		this.acceptValue = acceptValue;
	}
	
	/**
	 * Returns the acceptance value for this flow.
	 * @return The acceptance value for this flow
	 */
	public int getAcceptValue() {
		return acceptValue;
	}	
	
}
