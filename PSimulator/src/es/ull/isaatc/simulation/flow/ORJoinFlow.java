/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * A merge flow which allows all the true incoming branches to pass.
 * @author Iván Castilla Rodríguez
 */
public abstract class ORJoinFlow extends MergeFlow {

	/**
	 * Creates a new OR Join flow.
	 * @param model Model this flow belongs to
	 */
	public ORJoinFlow(Model model) {
		super(model);
	}
	
}
