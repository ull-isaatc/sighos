/**
 * 
 */
package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.Simulation;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a synchronization flow. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iv�n Castilla Rodr�guez
 */
public class InterleavedRoutingFlow extends PredefinedStructuredFlow {

	/**
	 * Creates a new InterleavedRoutingFlow 
	 * @param simul Simulation this flow belongs to
	 */
	public InterleavedRoutingFlow(Simulation simul) {
		super(simul);
		initialFlow = new ParallelFlow(simul);
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow(simul);
		finalFlow.setParent(this);
	}
}
