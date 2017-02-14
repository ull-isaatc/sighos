package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredFlow} whose initial step is a {@link ParallelFlow} and whose final step
 * is a {@link PartialJoinFlow}. Meets the Structured Partial Join pattern (WFP30). 
 * @author Yeray Callero
 */
public interface StructuredPartialJoinFlow extends PredefinedStructuredFlow {
}
