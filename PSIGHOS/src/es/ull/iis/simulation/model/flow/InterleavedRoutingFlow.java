/**
 * 
 */
package es.ull.iis.simulation.model.flow;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a synchronization flow. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iván Castilla Rodríguez
 */
public class InterleavedRoutingFlow extends PredefinedStructuredFlow {

	/**
	 * Creates a new InterleavedRoutingFlow 
	 */
	public InterleavedRoutingFlow() {
		super();
		initialFlow = new ParallelFlow();
		initialFlow.setParent(this);
		finalFlow = new SynchronizationFlow();
		finalFlow.setParent(this);
	}
}
