/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.EventSource;

/**
 * @author Iv�n Castilla
 *
 */
public interface EventSourceEngine<ES extends EventSource> {
	public void notifyEnd();
}
