/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.Element;


/**
 * A {@link Flow} which executes some kind of work. A task flow is both an {@link InitializerFlow}
 * and a {@link FinalizerFlow}. After being requested, it must perform some kind of work, and when 
 * this work is finished, it must notify its end.<p>
 * A {@link TaskFlow} includes a user-defined method {@link #afterFinalize(Element)}, which is invoked 
 * just after the task performed by the flow has been performed.<p>
 * @author Iván Castilla Rodríguez
 *
 */
public interface TaskFlow extends InitializerFlow, FinalizerFlow {
	/**
	 * Allows a user for adding customized code carried out after the task has finished. 
	 * @param e {@link Element} requesting this flow
	 */
	void afterFinalize(Element e);

}
