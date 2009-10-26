/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 * @param model Model this flow belongs to
	 */
	public MultiMergeFlow(Model model) {
		super(model);
	}
	
}
