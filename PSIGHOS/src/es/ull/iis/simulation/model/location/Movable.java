/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunctionParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Movable extends Located, TimeFunctionParams {
	void setLocation(Location location);
}
