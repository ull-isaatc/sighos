package es.ull.iis.simulation.model.flow;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a discriminator flow. Meets the Structured Discriminator pattern (WFP9). 
 * @author ycallero
 */
public class StructuredDiscriminatorFlow extends PredefinedStructuredFlow {
	/**
	 * Create a new StructureDiscriminatorMetaFlow.
	 */
	public StructuredDiscriminatorFlow() {
		super();
		initialFlow = new ParallelFlow();
		initialFlow.setParent(this);
		finalFlow = new DiscriminatorFlow();
		finalFlow.setParent(this);
	}

}
