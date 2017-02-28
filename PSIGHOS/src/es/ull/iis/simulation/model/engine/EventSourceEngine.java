/**
 * 
 */
package es.ull.iis.simulation.model.engine;

/**
 * @author Iv�n Castilla
 *
 */
public interface EventSourceEngine {
    /**
     * Informs the event source that it must finish its execution. 
     */
	void notifyEnd();
}
