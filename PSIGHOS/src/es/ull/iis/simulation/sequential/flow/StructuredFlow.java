package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.core.flow.InitializerFlow;
import es.ull.iis.simulation.core.flow.TaskFlow;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;



/**
 * A flow which can contain other flows. This kind of flows have a single entry point
 * <code>initialFlow</code> and a single exit point <code>finalFlow</code>, and can contain 
 * one or several internal branches.
 * @author ycallero
 *
 */
public abstract class StructuredFlow extends SingleSuccessorFlow implements TaskFlow<WorkThread>, es.ull.iis.simulation.core.flow.StructuredFlow<WorkThread> {
	/**	The entry point of the internal structure */
	protected InitializerFlow<WorkThread> initialFlow = null;
	/**	The exit point of the internal structure */
	protected FinalizerFlow<WorkThread> finalFlow = null;
	
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
	public void addPredecessor(Flow<WorkThread> newFlow) {
	}

	@Override
	public void afterFinalize(WorkThread wt) {
	}

	@Override
	public FinalizerFlow<WorkThread> getFinalFlow() {
		return finalFlow;
	}

	@Override
	public InitializerFlow<WorkThread> getInitialFlow() {
		return initialFlow;
	}
	
	
}
