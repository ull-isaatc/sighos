/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.Identifiable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ElementInstanceEngine extends Identifiable {
	void notifyResourcesAcquired();

}
