/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.ElementInstance;

/**
 * @author Iván Castilla
 *
 */
public interface ElementEngine extends EventSourceEngine {
	/**
	 * Notifies a new flow executor is waiting in an activity queue.
	 * @param fe Flow executor waiting in queue.
	 */
	void incInQueue(ElementInstance fe);

	/**
	 * Notifies a flow executor has finished waiting in an activity queue.
	 * @param fe Flow executor that was waiting in a queue.
	 */
	void decInQueue(ElementInstance fe);

	void notifyAvailableElement();
}
