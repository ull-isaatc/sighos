/**
 * 
 */
package es.ull.isaatc.simulation.core.flow;



/**
 * A {@link MultipleSuccessorFlow} which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iv�n Castilla Rodr�guez
 */
public interface ParallelFlow extends MultipleSuccessorFlow {
}
