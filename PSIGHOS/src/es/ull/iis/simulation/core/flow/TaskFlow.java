/**
 * 
 */
package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link Flow} which executes some kind of work. A task flow is both an {@link InitializerFlow}
 * and a {@link FinalizerFlow}. After being requested, it must perform some kind of work, and when 
 * this work is finished, it must notify its end.<p>
 * @author Iván Castilla Rodríguez
 *
 */
public interface TaskFlow<WT extends WorkThread<?>> extends InitializerFlow<WT>, FinalizerFlow<WT> {
	/**
	 * Finishes the associated task.
	 * @param wThread The work thread which requested this flow.
	 */
	void finish(WT wThread);


}
