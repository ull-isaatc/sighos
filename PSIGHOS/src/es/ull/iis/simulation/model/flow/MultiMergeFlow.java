/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Model;

/**
 * Creates an OR flow which allows all the true incoming branches to pass. 
 * Meets the Multi-Merge pattern (WFP8).
 * @author Iván Castilla Rodríguez
 */
public class MultiMergeFlow extends ORJoinFlow {

	/**
	 * Creates a new MultiMergeFlow.
	 */
	public MultiMergeFlow(Model model) {
		super(model);
	}
	
}
