package es.ull.isaatc.simulation.sequential.flow;

import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.Simulation;



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
	 * @param simul Simulation this flow belongs to.
	 */
	public StructuredFlow(Simulation simul) {
		super(simul);
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow newFlow) {
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.TaskFlow#afterFinalize(es.ull.isaatc.simulation.Element)
	 */
	public void afterFinalize(Element e) {
	}
	
}
