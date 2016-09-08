/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a synchronization flow. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iv�n Castilla Rodr�guez
 */
public class InterleavedRoutingFlow extends PredefinedStructuredFlow implements es.ull.iis.simulation.core.flow.InterleavedRoutingFlow {

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