/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Iv�n Castilla
 *
 */
public interface EventSourceEngine<ES extends EventSource> {
	public void notifyEnd();
}
