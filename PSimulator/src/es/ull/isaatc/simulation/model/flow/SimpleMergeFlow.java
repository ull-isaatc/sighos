package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * Creates an OR flow which allows all the true incoming branches to pass. The 
 * outgoing branch is activated only once when several incoming barnches arrive at
 * the same simulation time. 
 * Meets the Simple Merge pattern (WFP5).
 * @author ycallero
 *
 */
public class SimpleMergeFlow extends ORJoinFlow implements es.ull.isaatc.simulation.common.flow.SimpleMergeFlow {
	
	/**
	 * Creates a new SimpleMergeFlow.
	 * @param model Model this flow belongs to
	 */
	public SimpleMergeFlow(Model model) {
		super(model);
	}
}
