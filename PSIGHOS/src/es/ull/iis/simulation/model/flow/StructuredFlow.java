package es.ull.iis.simulation.model.flow;


/**
 * A flow which can contain other flows. This kind of flows have a single entry point
 * <code>initialFlow</code> and a single exit point <code>finalFlow</code>, and can contain 
 * one or several internal branches.
 * @author ycallero
 *
 */
public abstract class StructuredFlow extends SingleSuccessorFlow implements TaskFlow {
	/**	The entry point of the internal structure */
	protected InitializerFlow initialFlow = null;
	/**	The exit point of the internal structure */
	protected FinalizerFlow finalFlow = null;
	
	/**
	 * Creates a new structured flow with no initial nor final step.
	 */
	public StructuredFlow() {
	}

	@Override
	public void addPredecessor(Flow newFlow) {
	}

	@Override
	public void afterFinalize(FlowExecutor fe) {
	}

	public FinalizerFlow getFinalFlow() {
		return finalFlow;
	}

	public InitializerFlow getInitialFlow() {
		return initialFlow;
	}	
}
