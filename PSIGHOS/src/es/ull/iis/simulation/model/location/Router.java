/**
 * 
 */
package es.ull.iis.simulation.model.location;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Router {
	Location getNextLocationTo(Location currentLocation, Location finalLocation);
}
