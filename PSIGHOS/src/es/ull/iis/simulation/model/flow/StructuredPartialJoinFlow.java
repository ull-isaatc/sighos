package es.ull.iis.simulation.model.flow;

/**
 * A structured flow whose initial step is a parallel flow and whose final step
 * is a partial join flow. Meets the Structured Partial Join pattern (WFP30). 
 * @author ycallero
 */
public class StructuredPartialJoinFlow extends PredefinedStructuredFlow {
	
	/**
	 * Creates a new StructuredPartialJoinFlow.
	 */
	public StructuredPartialJoinFlow(int partialValue) {
		super();
		initialFlow = new ParallelFlow();
		initialFlow.setParent(this);
		finalFlow = new PartialJoinFlow(partialValue);
		finalFlow.setParent(this);
	}

}
