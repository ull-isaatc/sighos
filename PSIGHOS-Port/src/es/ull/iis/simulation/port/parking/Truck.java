/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 * 
 */
public class Truck extends Element {
	public static final int SIZE = 1;
	private final WaresType wares;
	private final TruckSource source;
	private final double maxLoad;
	private double currentLoad;
	private final Vessel servingVessel;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Truck(final Simulation simul, final ElementType et, final InitializerFlow initialFlow, final Vessel servingVessel, final TruckSource source) {
		super(simul, "TRUCK", et, initialFlow, Truck.SIZE, null);
		this.servingVessel = servingVessel;
		this.source = source;
		this.wares = servingVessel.getWares();
		this.maxLoad = wares.getTypicalTruckLoad().generate();
		this.currentLoad = maxLoad;		
	}

	/**
	 * @return the servingVessel
	 */
	public Vessel getServingVessel() {
		return servingVessel;
	}

	/**
	 * @return the source
	 */
	public TruckSource getSource() {
		return source;
	}

	public WaresType getWares() {
		return wares;
	}

	/**
	 * @return the maxLoad
	 */
	public double getMaxLoad() {
		return maxLoad;
	}

	/**
	 * @return the currentLoad
	 */
	public double getCurrentLoad() {
		return currentLoad;
	}

	/**
	 * Returns true if the truck is required for a transshipment operation; false otherwise
	 * @return True if the truck is required for a transshipment operation; false otherwise
	 */
	public boolean requiresTransshipmentOperation() {
		return servingVessel.bookTransshipmentOperation(maxLoad);
	}
	
	/**
	 * Performs a transshipment operation with the assigned vessel, trying to load/unload its maximum load
	 */
	public void performTransshipmentOperation() {
		currentLoad = servingVessel.performTransshipmentOperation(maxLoad);
	}
}
