/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunction;

/**
 * A location representing a path between two or more places ({@link Node}). Useful for roads.
 * The capacity of the path is the product of the number of lanes and the capacity per lane 
 * @author Iván Castilla Rodríguez
 *
 */
public class Path extends Location {
	/** Number of lanes of the path */
	private final int nLanes;

	/**
	 * Creates a path with capacity constrains 
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 * @param capacityPerLane Total capacity of each lane
	 * @param nLanes Number of lanes of the path
	 */
	public Path(String description, TimeFunction delayAtExit, int capacityPerLane, int nLanes) {
		super(description, delayAtExit, capacityPerLane * nLanes);
		this.nLanes = nLanes; 
	}

	/**
	 * Creates a path with no capacity constrains
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 */
	public Path(String description, TimeFunction delayAtExit) {
		super(description, delayAtExit);
		this.nLanes = Integer.MAX_VALUE; 
	}

	/**
	 * Creates a path with capacity constrains and no time to exit.
	 * @param description A brief description of the location
	 * @param capacityPerLane Total capacity of each lane
	 * @param nLanes Number of lanes of the path
	 */
	public Path(String description, int capacityPerLane, int nLanes) {
		super(description, capacityPerLane * nLanes);
		this.nLanes = nLanes; 
	}
	
	/**
	 * Creates a path with no capacity constrains and no time to exit
	 * @param description A brief description of the location
	 */
	public Path(String description) {
		super(description);
		this.nLanes = Integer.MAX_VALUE; 
	}

	/**
	 * Returns the total capacity of each lane of the path
	 * @return the total capacity of each lane of the path
	 */
	public int getCapacityPerLane() {
		return (nLanes == Integer.MAX_VALUE) ? Integer.MAX_VALUE : getCapacity() / nLanes;
	}

	/**
	 * Returns the number of lanes of the path
	 * @return the number of lanes of the path
	 */
	public int getnLanes() {
		return nLanes;
	}

	@Override
	public Location getLocation() {
		return this;
	}
}
