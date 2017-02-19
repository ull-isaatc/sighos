/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * A simulation object with an attached queue
 * 
 * @author Iván Castilla
 *
 */
public interface QueuedObject<T> {
    /**
     * Add an object to the queue.
     * @param obj Object added
     */
    public void queueAdd(T obj);
    
    /**
     * Remove a specific object from the queue.
     * @param obj Object that must be removed from the queue
     */
    public void queueRemove(T obj);

    /**
     * Returns the size of the queue 
     * @return the size of the queue
     */
    public int getQueueSize();

}
