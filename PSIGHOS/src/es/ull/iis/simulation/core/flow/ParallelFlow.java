/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link MultipleSuccessorFlow} which creates a new work thread per outgoing branch.
 * Meets the Parallel Split pattern (WFP2) 
 * @author Iv�n Castilla Rodr�guez
 */
public interface ParallelFlow<WT extends WorkThread<?>> extends MultipleSuccessorFlow<WT> {
}
