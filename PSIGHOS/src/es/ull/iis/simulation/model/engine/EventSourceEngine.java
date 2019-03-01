/**
 * 
 */
package es.ull.iis.simulation.model.engine;

/**
 * A class capable of generating events
 * @author Iván Castilla
 *
 */
public interface EventSourceEngine {
    /**
     * Informs the event source that it must finish its execution. 
     */
	void notifyEnd();
}
