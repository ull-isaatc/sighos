/**
 * 
 */
package es.ull.isaatc.simulation.common.flow;


/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a synchronization flow. Meets the Interleaved Routing pattern (WFP40) if all the
 * activities are presential.
 * @author Iv�n Castilla Rodr�guez
 */
public interface InterleavedRoutingFlow extends PredefinedStructuredFlow {
}
