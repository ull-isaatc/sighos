package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;



/**
 * A flow which can contain other flows. This kind of flows have a single entry point
 * <code>initialFlow</code> and a single exit point <code>finalFlow</code>, and can contain 
 * one or several internal branches.
 * @author ycallero
 *
 */
public abstract class StructuredFlow extends SingleSuccessorFlow implements TaskFlow, es.ull.iis.simulation.core.flow.StructuredFlow {
	/**	The entry point of the internal structure */
	protected InitializerFlow initialFlow = null;
	/**	The exit point of the internal structure */
	protected FinalizerFlow finalFlow = null;
	
	/**
	 * Creates a new structured flow with no initial nor final step.
	 * @param simul Simulation this flow belongs to.
	 */
	public StructuredFlow(Simulation simul) {
		super(simul);
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Flow#addPredecessor(es.ull.iis.simulation.Flow)
	 */
	public void addPredecessor(es.ull.iis.simulation.core.flow.Flow newFlow) {
	}

	@Override
	public void afterFinalize(es.ull.iis.simulation.core.WorkThread<?> wt) {
	}

	@Override
	public FinalizerFlow getFinalFlow() {
		return finalFlow;
	}

	@Override
	public InitializerFlow getInitialFlow() {
		return initialFlow;
	}
	
	
}
