/**
 * 
 */
package es.ull.iis.simulation.model.location;

/**
 * Class that calculates the steps that an entity has to follow to reach a destination
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public interface Router {
	/**
	 * Returns the next location in the way for an entity trying to reach a destination; null if the destination is not reachable from 
	 * the current location 
	 * @param entity Entity moving from its current location to destination
	 * @param destination Destination of the entity
	 * @return the next location in the way for an entity trying to reach a destination; null if the destination is not reachable from 
	 * the current location
	 */
	Location getNextLocationTo(final Movable entity, final Location destination);
}
