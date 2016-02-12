/**
 * 
 */
package es.ull.iis.simulation.parallel.flow;

import es.ull.iis.simulation.parallel.WorkThread;

/**
 * A flow which executes some kind of work. A task flow is both an initializer and a finalizer
 * flow. After being requested, it must perform some kind of work, and when this work is finished, 
 * it must notify its end. The method <code>finish</code> includes the code executed when the 
 * task has finished, and it must invoke <code>afterFinalize</code>, which contains user code.<p>
 * A false work thread requesting a task flow directly pass to the successor flow.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TaskFlow extends es.ull.iis.simulation.core.flow.TaskFlow, InitializerFlow, FinalizerFlow {
	/**
	 * Finishes the associated task.
	 * @param wThread The work thread which requested this flow.
	 */
	void finish(WorkThread wThread);

}
