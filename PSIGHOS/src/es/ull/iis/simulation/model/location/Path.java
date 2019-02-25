/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Path extends Location {
	private final int nLanes;

	/**
	 * 
	 */
	public Path(String description, TimeFunction delayAtExit, int sizePerLane, int nLanes) {
		super(description, delayAtExit, sizePerLane * nLanes);
		this.nLanes = nLanes; 
	}

	/**
	 * A path with no capacity constrains
	 */
	public Path(String description, TimeFunction delayAtExit) {
		super(description, delayAtExit);
		this.nLanes = Integer.MAX_VALUE; 
	}

	public int getSizePerLane() {
		return (nLanes == Integer.MAX_VALUE) ? Integer.MAX_VALUE : getSize() / nLanes;
	}

	/**
	 * @return the nLanes
	 */
	public int getnLanes() {
		return nLanes;
	}

	@Override
	public Location getLocation() {
		return this;
	}
}
