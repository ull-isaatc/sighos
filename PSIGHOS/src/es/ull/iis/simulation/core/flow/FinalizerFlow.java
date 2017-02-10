package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} which finishes an execution branch. Only finalizer flows can be used as the last
 * step in a flow structure. 
 * A {@link FinalizerFlow} includes a user-defined method {@link #afterFinalize(WorkThread)}, which is invoked 
 * just after the task performed by the flow has been performed.<p>
 * @author Iván Castilla Rodríguez
 */
public interface FinalizerFlow<WT extends WorkThread<?>> extends Flow<WT> {
	/**
	 * Allows a user for adding customized code carried out after the flow has finished. 
	 * @param wt {@link WorkThread} requesting this flow
	 */
	void afterFinalize(WT wt);

}
