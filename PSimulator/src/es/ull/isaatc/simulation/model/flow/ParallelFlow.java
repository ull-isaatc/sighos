/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow implements es.ull.isaatc.simulation.common.flow.ParallelFlow {

	/**
	 * Creates a new ParallelFlow
	 * @param model Model this flow belongs to
	 */
	public ParallelFlow(Model model) {
		super(model);
	}
}
