package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} which finishes an execution branch. Only finalizer flows can be used as the last
 * step in a flow structure. 
 * A {@link FinalizerFlow} includes a user-defined method {@link #afterFinalize(WorkThread)}, which is invoked 
 * just after the task performed by the flow has been performed.<p>
 * @author Iván Castilla Rodríguez
 */
public interface FinalizerFlow extends Flow {
	/**
	 * Allows a user for adding customized code carried out after the flow has finished. 
	 * @param fe {@link FlowExecutor} requesting this flow
	 */
	void afterFinalize(FlowExecutor fe);

}
