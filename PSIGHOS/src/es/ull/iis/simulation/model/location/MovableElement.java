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
 * @author Iván Castilla Rodríguez
 *
 */
public class MovableElement extends Element implements Movable {
	private Location currentLocation;
	private final int size;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public MovableElement(Simulation simul, ElementType elementType, InitializerFlow initialFlow, Location initLocation, int size) {
		super(simul, elementType, initialFlow);
		this.size = size;
		currentLocation = initLocation;
		simul.notifyInfo(new ElementLocationInfo(simul, this, initLocation, ElementLocationInfo.Type.START, getTs()));
	}

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public MovableElement(Simulation simul, ElementType elementType, InitializerFlow initialFlow, Location initLocation) {
		this(simul, elementType, initialFlow, initLocation, 0);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.location.Located#getSize()
	 */
	@Override
	public int getSize() {
		return size;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.model.location.Located#getLocation()
	 */
	@Override
	public Location getLocation() {
		return currentLocation;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.function.TimeFunctionParams#getTime()
	 */
	@Override
	public double getTime() {
		return getTs();
	}

	@Override
	public void setLocation(Location location) {
		simul.notifyInfo(new ElementLocationInfo(simul, this, currentLocation, ElementLocationInfo.Type.LEAVE, getTs()));
		currentLocation = location;
		simul.notifyInfo(new ElementLocationInfo(simul, this, currentLocation, ElementLocationInfo.Type.ARRIVE, getTs()));
	}

}
