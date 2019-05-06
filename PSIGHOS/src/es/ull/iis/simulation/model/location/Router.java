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
	/** Abstract location to indicate that the entity has to wait for a condition to meet before moving */
	static final Location COND_WAIT_LOCATION = new Location("Abstract location to indicate that the entity has to wait") {
		@Override
		public Location getLocation() {
			return this;
		}
	};
	/** Abstract location for an unreachable destination */
	static final Location UNREACHABLE_LOCATION = new Location("Abstract location for an unreachable destination") {
		@Override
		public Location getLocation() {
			return this;
		}
	};
	/**
	 * Returns the next location in the way for an entity trying to reach a destination; null if the destination is not reachable from 
	 * the current location 
	 * @param entity Entity moving from its current location to destination
	 * @param destination Destination of the entity
	 * @return the next location in the way for an entity trying to reach a destination; {@link #UNREACHABLE_LOCATION} if the destination 
	 * is not reachable from the current location; and {@link #COND_WAIT_LOCATION} if the entity has to wait before moving
	 */
	Location getNextLocationTo(final Movable entity, final Location destination);

	/**
	 * Returns true if the location is an unreachable location
	 * @param loc Location
	 * @return true if the location is an unreachable location
	 */
	public static boolean isUnreachableLocation(Location loc) {
		return UNREACHABLE_LOCATION.equals(loc);
	}

	/**
	 * Returns true if the location represents a conditional waiting
	 * @param loc Location
	 * @return true if the location represents a conditional waiting
	 */
	public static boolean isConditionalWaitLocation(Location loc) {
		return COND_WAIT_LOCATION.equals(loc);
	}
}
