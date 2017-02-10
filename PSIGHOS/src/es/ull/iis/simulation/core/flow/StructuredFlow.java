package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} which can contain other flows. This kind of flows have a single entry point
 * (<tt>initialFlow</tt>) and a single exit point (<tt>finalFlow</tt>), and can contain 
 * one or several internal branches.
 * @author Yeray Callero
 */
public interface StructuredFlow<WT extends WorkThread<?>> extends SingleSuccessorFlow<WT>, TaskFlow<WT> {
	/**	The entry point to the internal structure */
	InitializerFlow<WT> getInitialFlow();
	/**	The exit point of the internal structure */
	FinalizerFlow<WT> getFinalFlow();
}
