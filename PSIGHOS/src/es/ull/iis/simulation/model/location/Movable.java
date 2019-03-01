/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunctionParams;

/**
 * An object that can move from one location to another
 * @author Iván Castilla Rodríguez
 *
 */
public interface Movable extends Located, TimeFunctionParams {
	/**
	 * Sets the current location of the object
	 * @param location The current location of the object 
	 */
	void setLocation(final Location location);
}
