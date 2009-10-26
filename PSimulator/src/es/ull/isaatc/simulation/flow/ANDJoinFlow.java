/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * A merge flow which allows only one of the incoming branches to pass. Which one
 * passes depends on the <code>acceptValue</code>.
 * @author Iván Castilla Rodríguez
 */
public abstract class ANDJoinFlow extends MergeFlow {
	/** The number of branches which have to arrive to pass the control thread */
	protected int acceptValue;

	/**
	 * Creates a new AND flow.
	 * @param model Model this flow belongs to
	 */
	public ANDJoinFlow(Model model) {
		super(model);
	}
	
	/**
	 * Creates a new AND flow
	 * @param simul Simulation this flow belongs to
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Model model, int acceptValue) {
		super(model);
		this.acceptValue = acceptValue;
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param model Model this flow belongs to
	 * @param safe True for safe context; false in other case
	 */
	public ANDJoinFlow(Model model, boolean safe) {
		super(model, safe);
	}
	
	/**
	 * Create a new AND Flow which can be used in a safe context or a general one.
	 * @param model Model this flow belongs to
	 * @param safe True for safe context; false in other case
	 * @param acceptValue The number of branches which have to arrive to pass the control thread
	 */
	public ANDJoinFlow(Model model, boolean safe, int acceptValue) {
		super(model, safe);
		this.acceptValue = acceptValue;
	}
	
}
