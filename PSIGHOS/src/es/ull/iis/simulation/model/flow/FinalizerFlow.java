package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.ElementInstance;

/**
 * A {@link Flow} which finishes an execution branch. Only finalizer flows can be used as the last
 * step in a flow structure. 
 * A {@link FinalizerFlow} includes a user-defined method {@link #afterFinalize(ElementInstance)}, which is invoked 
 * just after the task performed by the flow has been performed.<p>
 * @author Iván Castilla Rodríguez
 */
public interface FinalizerFlow extends Flow {
	/**
	 * Allows a user for adding customized code carried out after the flow has finished. 
	 * @param ei {@link ElementInstance} requesting this flow
	 */
	void afterFinalize(ElementInstance ei);

}
