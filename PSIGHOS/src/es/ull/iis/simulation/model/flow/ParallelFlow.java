/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Model;

/**
 * A multiple successor flow which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iván Castilla Rodríguez
 */
public class ParallelFlow extends MultipleSuccessorFlow {

	/**
	 * Creates a new ParallelFlow
	 */
	public ParallelFlow(Model model) {
		super(model);
	}
}
