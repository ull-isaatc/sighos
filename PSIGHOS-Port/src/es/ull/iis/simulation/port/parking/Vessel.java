/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.port.parking.VesselCreator.VesselGenerationInfo;

/**
 * @author Iván Castilla
 *
 */
public class Vessel extends Element {
	private final WaresType wares;
	private final double initLoad;
	private double currentLoad;
	/** The amount of load not yet assigned to the vessel */
	private double notAssignedLoad;
	private final int vesselId;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Vessel(Simulation simul, int vesselId, WaresType wares, VesselGenerationInfo info) {
		super(simul, "VESSEL", info);
		this.vesselId = vesselId;
		this.wares = wares;
		this.initLoad = wares.getTypicalVesselLoad().generate();
		this.currentLoad = initLoad;
		this.notAssignedLoad = initLoad;
	}

	public Vessel(Simulation simul, int vesselId, WaresType wares, ElementType elementType, InitializerFlow initialFlow, Node initialLocation) {
		super(simul, "VESSEL", elementType, initialFlow, PortParkingModel.VESSEL_SIZE, initialLocation);
		this.vesselId = vesselId;
		this.wares = wares;
		this.initLoad = wares.getTypicalVesselLoad().generate();
		this.currentLoad = initLoad;
		this.notAssignedLoad = initLoad;
	}
	
	@Override
	public String toString() {
		return "[VESSEL" + vesselId + "]";
	}
	
	/**
	 * @return the vesselId
	 */
	public int getVesselId() {
		return vesselId;
	}

	public WaresType getWares() {
		return wares;
	}

	public double getInitLoad() {
		return initLoad;
	}

	/**
	 * @return the currentLoad
	 */
	public double getCurrentLoad() {
		return currentLoad;
	}

	/**
	 * Books in advance a transshipment operation (either a load or unload). 
	 * This is useful to know whether a truck must return to continue performing operations.
	 * @param quantity Amount of load to move from vessel to truck or viceversa
	 * @return True if there is remaining load to work with; false otherwise
	 */
	public boolean bookTransshipmentOperation(double quantity) {
		final double actual = (quantity > notAssignedLoad) ? notAssignedLoad : quantity;
		notAssignedLoad -= actual;
//		System.out.println(getTs() + "\tBOOKED\t" + actual + "/" + notAssignedLoad);
		return (actual > PortParkingModel.MIN_LOAD);
	}
	
	/**
	 * Performs a transshipment operation (either a load or unload)
	 * @param quantity Amount of load to move from vessel to truck or viceversa
	 * @return The actual amount that can be moved (in case the vessel has not enough remaining load)
	 */
	public double performTransshipmentOperation(double quantity) {
		final double actual = (quantity > currentLoad) ? currentLoad : quantity;
		currentLoad -= actual;
//		System.out.println(getTs() + "\tUNLOADED\t" + actual + "/" + currentLoad);
		return actual;
	}
	
	/**
	 * Returns true if the vessel is empty, i.e., if there is no more load to move
	 * @return True if the vessel is empty, i.e., if there is no more load to move
	 */
	public boolean isEmpty() {
		return (currentLoad < PortParkingModel.MIN_LOAD);
	}
	
	/**
	 * Returns true if the vessel is ready to start transshipment operations, i.e., if the vessel is at a quay
	 * @return True if the vessel is ready to start transshipment operations, i.e., if the vessel is at a quay
	 */
	public boolean isReadyForTransshipment() {
		for (QuayType quay : QuayType.values())
			if (quay.getLocation().getNode().equals(getLocation()))
				return true;
		return false;
	}
}
