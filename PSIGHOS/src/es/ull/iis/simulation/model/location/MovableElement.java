/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.simulation.info.ElementLocationInfo;
import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * An {@link Element} that can move from a {@link Location} to another.
 * @author Iván Castilla Rodríguez
 *
 */
public class MovableElement extends Element implements Movable {
	/** The current location of the element */
	private Location currentLocation;
	/** The next location of the element */
	private Location nextLocation;
	/** The size of the element */
	private final int size;

	/**
	 * Creates a movable element with a specified size
	 * @param simul The simulation this element belongs to
	 * @param elementType The element type
	 * @param initialFlow The initial flow for the element
	 * @param size The size of the element
	 */
	public MovableElement(Simulation simul, ElementType elementType, InitializerFlow initialFlow, int size) {
		super(simul, elementType, initialFlow);
		this.size = size;
		currentLocation = null;
		nextLocation = null;
	}

	/**
	 * Creates a movable element with a no size
	 * @param simul The simulation this element belongs to
	 * @param elementType The element type
	 * @param initialFlow The initial flow for the element
	 */
	public MovableElement(Simulation simul, ElementType elementType, InitializerFlow initialFlow) {
		this(simul, elementType, initialFlow, 0);
	}

	@Override
	public int getCapacity() {
		return size;
	}

	@Override
	public Location getLocation() {
		return currentLocation;
	}

	@Override
	public double getTime() {
		return getTs();
	}

	@Override
	public void setLocation(Location location) {
		if (currentLocation == null) {
			simul.notifyInfo(new ElementLocationInfo(simul, this, location, ElementLocationInfo.Type.START, getTs()));
			currentLocation = location;
		}
		else {
			simul.notifyInfo(new ElementLocationInfo(simul, this, currentLocation, ElementLocationInfo.Type.LEAVE, getTs()));
			currentLocation = location;
			simul.notifyInfo(new ElementLocationInfo(simul, this, currentLocation, ElementLocationInfo.Type.ARRIVE, getTs()));
		}
	}

	/**
	 * Returns the next location for this element
	 * @return the next location for this element
	 */
	public Location getNextLocation() {
		return nextLocation;
	}

	/**
	 * Sets the next location for this element
	 * @param nextLocation the next location for this element
	 */
	public void setNextLocation(Location nextLocation) {
		this.nextLocation = nextLocation;
	}
}
