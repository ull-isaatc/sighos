/**
 * 
 */
package es.ull.isaatc.simulation.flow;


/**
 * A {@link StructuredFlow} whose initial step is a {@link ParallelFlow} and whose final step
 * is a {@link SynchronizationFlow}. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iv�n Castilla Rodr�guez
 */
public interface InterleavedRoutingFlow extends PredefinedStructuredFlow {
}
