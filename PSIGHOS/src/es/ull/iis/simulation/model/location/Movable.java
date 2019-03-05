/**
 * 
 */
package es.ull.iis.simulation.model.location;

/**
 * An object that can move from one location to another
 * @author Iván Castilla Rodríguez
 *
 */
public interface Movable extends Located {
	/**
	 * Sets the current location of the object
	 * @param location The current location of the object 
	 */
	void setLocation(final Location location);
	void notifyLocationAvailable(final Location location);
}
