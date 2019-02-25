/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunctionParams;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Movable extends Located, TimeFunctionParams {
	void setLocation(Location location);
}
