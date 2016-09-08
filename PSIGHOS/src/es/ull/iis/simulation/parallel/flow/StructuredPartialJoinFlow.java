package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.Simulation;


/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a partial join flow. Meets the Structured Partial Join pattern (WFP30). 
 * @author ycallero
 */
public class StructuredPartialJoinFlow extends PredefinedStructuredFlow implements es.ull.iis.simulation.core.flow.StructuredPartialJoinFlow {
	
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