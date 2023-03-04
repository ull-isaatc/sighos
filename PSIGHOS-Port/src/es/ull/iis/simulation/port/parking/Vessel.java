/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.port.parking.VesselCreator.VesselGenerationInfo;

/**
 * @author Iv�n Castilla
 *
 */
public class Vessel extends Element {
	/** A collection of different orders to load/unload certain amount of wares */
	private final ArrayList<VesselTransshipmentOrder> orders;	
	/** The sum of wares load */
	private final double initLoad;
	/** The current load of the vesel */
	private double currentLoad;
	/** An identifier for vessels */ 
	private final int vesselId;
	/** Potential quays for this vessel to dock */
	private final EnumSet<QuayType> potentialQuays;
	private final WaresType mainWares;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Vessel(Simulation simul, int vesselId, ArrayList<VesselTransshipmentOrder> orders, VesselGenerationInfo info) {
		super(simul, "VESSEL", info);
		this.vesselId = vesselId;
		this.orders = orders;
		this.mainWares = precomputePotentialQuaysAndLoad(); 
		this.potentialQuays = mainWares.getPotentialQuays(); 
		this.initLoad = currentLoad;
	}

	public Vessel(Simulation simul, int vesselId, ArrayList<VesselTransshipmentOrder> orders, ElementType elementType, InitializerFlow initialFlow, Node initialLocation) {
		super(simul, "VESSEL", elementType, initialFlow, PortParkingModel.VESSEL_SIZE, initialLocation);
		this.vesselId = vesselId;
		this.orders = orders;
		this.mainWares = precomputePotentialQuaysAndLoad(); 
		this.potentialQuays = mainWares.getPotentialQuays(); 
		this.initLoad = currentLoad;
	}
	
	@Override
	public String toString() {
		return "[VESSEL" + vesselId + "]";
	}
	
	private WaresType precomputePotentialQuaysAndLoad() {		
		double maxOp = 0.0;
		WaresType major = null;
		this.currentLoad  = 0.0;
		for (TransshipmentOrder order : orders) {
			this.currentLoad += order.getTones(); 
			if (order.getTones() > maxOp) {
				maxOp = order.getTones();
				major = order.getWares();
			}
		}
		return major;
	}
	
	/**
	 * @return the vesselId
	 */
	public int getVesselId() {
		return vesselId;
	}

	public ArrayList<VesselTransshipmentOrder> getOrders() {
		return orders;
	}

	public WaresType getMainWares() {
		return mainWares;
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
	public TransshipmentOrder getTransshipmentOrderForTruck(Truck truck) {
		for (VesselTransshipmentOrder order : orders) {
			TransshipmentOrder orderForTruck = order.getTransshipmentOrderForTruck(truck);
			if (orderForTruck != null)
				return orderForTruck;
		}
//		System.out.println(getTs() + "\tBOOKED\t" + actual + "/" + notAssignedLoad);
		return null;
	}
	
	/**
	 * Performs a transshipment operation (either a load or unload)
	 * @param quantity Amount of load to move from vessel to truck or viceversa
	 * @return The actual amount that can be moved (in case the vessel has not enough remaining load)
	 */
	public boolean performTransshipmentOperation(TransshipmentOrder truckOrder) {
		for (VesselTransshipmentOrder order : orders) {
			if (order.performTransshipmentOrder(truckOrder)) {
				currentLoad -= truckOrder.getTones();
				return true;
			}
		}
//		System.out.println(getTs() + "\tUNLOADED\t" + actual + "/" + currentLoad);
		return false;
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

	
	/**
	 * TODO: Para elegir muelle, de momento, solo se tiene en cuenta su relaci�n con la mercanc�a mayoritaria del barco, pero hay
	 * claras diferencias en los datos seg�n el tipo de operaci�n, tambi�n. 
	 * 
	 * @return
	 */
	public EnumSet<QuayType> getPotentialQuays() {
		return potentialQuays;
	}
}
