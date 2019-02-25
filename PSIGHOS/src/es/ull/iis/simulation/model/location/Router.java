/**
 * 
 */
package es.ull.iis.simulation.model.location;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Router {
	Location getNextLocationTo(Location currentLocation, Location finalLocation);
}
