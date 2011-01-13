package es.ull.isaatc.simulation.sequential.flow;

import es.ull.isaatc.simulation.sequential.Simulation;


/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a discriminator flow. Meets the Structured Discriminator pattern (WFP9). 
 * @author ycallero
 */
public class StructuredDiscriminatorFlow extends PredefinedStructuredFlow implements es.ull.isaatc.simulation.core.flow.StructuredDiscriminatorFlow {
	/**
	 * Create a new StructureDiscriminatorMetaFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public StructuredDiscriminatorFlow(Simulation simul) {
		super(simul);
		initialFlow = new ParallelFlow(simul);
		initialFlow.setParent(this);
		finalFlow = new DiscriminatorFlow(simul);
		finalFlow.setParent(this);
	}

}
