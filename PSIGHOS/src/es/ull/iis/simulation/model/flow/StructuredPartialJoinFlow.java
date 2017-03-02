package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a partial join flow. Meets the Structured Partial Join pattern (WFP30). 
 * @author ycallero
 */
public class StructuredPartialJoinFlow extends PredefinedStructuredFlow {
	
	/**
	 * Creates a new StructuredPartialJoinFlow.
	 */
	public StructuredPartialJoinFlow(Simulation model, int partialValue) {
		super(model);
		initialFlow = new ParallelFlow(model);
		initialFlow.setParent(this);
		finalFlow = new PartialJoinFlow(model, partialValue);
		finalFlow.setParent(this);
	}

}
