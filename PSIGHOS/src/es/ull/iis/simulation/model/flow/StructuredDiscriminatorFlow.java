package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a discriminator flow. Meets the Structured Discriminator pattern (WFP9). 
 * @author ycallero
 */
public class StructuredDiscriminatorFlow extends PredefinedStructuredFlow {
	/**
	 * Create a new StructureDiscriminatorMetaFlow.
	 */
	public StructuredDiscriminatorFlow(Simulation model) {
		super(model);
		initialFlow = new ParallelFlow(model);
		initialFlow.setParent(this);
		finalFlow = new DiscriminatorFlow(model);
		finalFlow.setParent(this);
	}

}
