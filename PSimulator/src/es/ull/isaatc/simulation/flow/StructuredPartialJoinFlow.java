package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;


/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a partial join flow. Meets the Structured Partial Join pattern (WFP30). 
 * @author ycallero
 */
public class StructuredPartialJoinFlow extends PredefinedStructuredFlow {
	
	/**
	 * Creates a new StructuredPartialJoinFlow.
	 * @param model Simulation this flow belongs to
	 */
	public StructuredPartialJoinFlow(Model model, int partialValue) {
		super(model);
		initialFlow = new ParallelFlow(model);
		initialFlow.setParent(this);
		finalFlow = new PartialJoinFlow(model, partialValue);
		finalFlow.setParent(this);
	}

}
