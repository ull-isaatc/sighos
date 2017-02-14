package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredFlow} whose initial step is a {@link ParallelFlow} and whose final step
 * is a {@link DiscriminatorFlow}. Meets the Structured Discriminator pattern (WFP9). 
 * @author Yeray Callero
 */
public interface StructuredDiscriminatorFlow extends PredefinedStructuredFlow {
}
