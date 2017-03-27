/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.flow.Flow;

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
	
	/**
	 * Acquires a semaphore associated to a specific flow. 
	 * Useful only for parallel implementations
	 * @param flow The flow to be requested
	 */
	void waitProtectedFlow(Flow flow);
	
	/**
	 * Releases a semaphore associated to a specific flow
	 * Useful only for parallel implementations
	 * @param flow The flow to be requested
	 */
	void signalProtectedFlow(Flow flow);
	
	ElementInstanceEngine getElementInstance(ElementInstance ei);
}
