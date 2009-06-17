package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.Simulation;


/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a partial join flow. Meets the Structured Partial Join pattern (WFP30). 
 * @author ycallero
 */
public class StructuredPartialJoinFlow extends PredefinedStructuredFlow {
	
	/**
	 * Creates a new StructuredPartialJoinFlow.
	 * @param simul Simulation this flow belongs to
	 */
	public StructuredPartialJoinFlow(Simulation simul, int partialValue) {
		super(simul);
		initialFlow = new ParallelFlow(simul);
		initialFlow.setParent(this);
		finalFlow = new PartialJoinFlow(simul, partialValue);
		finalFlow.setParent(this);
	}

}
