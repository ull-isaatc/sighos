package es.ull.iis.simulation.core.flow;

/**
 * A {@link Flow} which can contain other flows. This kind of flows have a single entry point
 * (<tt>initialFlow</tt>) and a single exit point (<tt>finalFlow</tt>), and can contain 
 * one or several internal branches.
 * @author Yeray Callero
 */
public interface StructuredFlow extends SingleSuccessorFlow, TaskFlow {
	/**	The entry point to the internal structure */
	InitializerFlow getInitialFlow();
	/**	The exit point of the internal structure */
	FinalizerFlow getFinalFlow();
}