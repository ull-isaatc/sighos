/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * @author Iván Castilla
 *
 */
public interface EventSourceEngine<ES extends EventSource> {
	public void notifyEnd();
}
