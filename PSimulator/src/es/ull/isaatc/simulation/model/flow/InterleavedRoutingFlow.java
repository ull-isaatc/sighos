/**
 * 
 */
package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a synchronization flow. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iván Castilla Rodríguez
 */
public class InterleavedRoutingFlow extends PredefinedStructuredFlow implements es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow {

	/**
	 * Creates a new InterleavedRoutingFlow 
	 * @param model Model this flow belongs to.
	 */
	public InterleavedRoutingFlow(Model model) {
		super(model);
		initialFlow = new ParallelFlow(model);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(model);
		finalFlow.setParent(this);
	}
}
