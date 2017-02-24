/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.model.flow.FlowExecutor;

/**
 * @author Iván Castilla
 *
 */
public interface ElementEngine {
	/**
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	void incInQueue(FlowExecutor fe);

	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	void decInQueue(FlowExecutor fe);

	void next(FlowExecutor fe);

	void request(FlowExecutor fe);
	
	void finish(FlowExecutor fe);
}
