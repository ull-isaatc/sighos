package es.ull.isaatc.simulation.common.flow;

/**
 * A flow which can contain other flows. This kind of flows have a single entry point
 * <code>initialFlow</code> and a single exit point <code>finalFlow</code>, and can contain 
 * one or several internal branches.
 * @author ycallero
 *
 */
public interface StructuredFlow extends SingleSuccessorFlow, TaskFlow {
	/**	The entry point of the internal structure */
	InitializerFlow getInitialFlow();
	/**	The exit point of the internal structure */
	FinalizerFlow getFinalFlow();
}
