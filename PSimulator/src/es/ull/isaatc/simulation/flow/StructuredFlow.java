package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.model.Model;



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
	 * @param model Model this flow belongs to
	 */
	public StructuredFlow(Model model) {
		super(model);
		userMethods.put("afterFinalize", "");
	}
	
	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#addPredecessor(es.ull.isaatc.simulation.Flow)
	 */
	public void addPredecessor(Flow newFlow) {
	}
}
