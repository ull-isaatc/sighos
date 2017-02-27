/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.FlowExecutor;

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

	void notifyAvailableElement();
}
