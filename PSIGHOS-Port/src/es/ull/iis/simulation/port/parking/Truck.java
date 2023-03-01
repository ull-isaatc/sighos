/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;

/**
 * @author Iván Castilla Rodríguez
 * 
 */
public class Truck extends Element {
	private final WaresType wares;
	private final TruckSource source;
	private final double maxLoad;
	private double currentLoad;
	private final Vessel servingVessel;
	private final int truckId;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Truck(final Simulation simul, int truckId, final ElementType et, final InitializerFlow initialFlow, final Vessel servingVessel, final TruckSource source) {
		super(simul, "TRUCK", et, initialFlow, PortParkingModel.TRUCK_SIZE, null);
		this.truckId = truckId;
		this.servingVessel = servingVessel;
		this.source = source;
		this.wares = servingVessel.getWares();
		this.maxLoad = PortParkingModel.TRUCK_MAX_LOAD.generate();
		this.currentLoad = maxLoad;		
	}

	@Override
	public String toString() {
		return "[TRUCK" + servingVessel.getVesselId() + "_" + truckId + "]";
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
