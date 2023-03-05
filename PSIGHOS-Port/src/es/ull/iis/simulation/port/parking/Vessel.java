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
import es.ull.iis.simulation.port.parking.TransshipmentOrder.OperationType;

/**
 * @author Iván Castilla
 *
 */
public class Vessel extends Element {
	/** A collection of different orders to load certain amount of wares */
	private final ArrayList<VesselTransshipmentOrder> loadOperations;	
	/** A collection of different orders to unload certain amount of wares */
	private final ArrayList<VesselTransshipmentOrder> unloadOperations;
	/** The current work load of the vesel (in tones), for either LOAD or UNLOAD operations */
	private final double[] pendingTones;
	/** The initial amount of work load of the vessel (in tones), for either LOAD or UNLOAD operations */
	private final double[] initTones;
	/** An identifier for vessels */ 
	private final int vesselId;
	/** Potential quays for this vessel to dock */
	private final EnumSet<QuayType> potentialQuays;
	private final WaresType mainWares;

	public Vessel(Simulation simul, int vesselId, ArrayList<VesselTransshipmentOrder> operations, ElementType elementType, InitializerFlow initialFlow, Node initialLocation) {
		super(simul, "VESSEL", elementType, initialFlow, PortParkingModel.VESSEL_SIZE, initialLocation);
		this.vesselId = vesselId;
		this.loadOperations = new ArrayList<>();
		this.unloadOperations = new ArrayList<>();
		this.pendingTones = new double[2];
		this.initTones = new double[2];
		this.mainWares = precomputePotentialQuaysAndLoad(operations); 
		this.potentialQuays = mainWares.getPotentialQuays(); 
	}
	
	@Override
	public String toString() {
		return "[VESSEL" + vesselId + "]";
	}
	
	private WaresType precomputePotentialQuaysAndLoad(ArrayList<VesselTransshipmentOrder> operations) {		
		double maxOp = 0.0;
		WaresType major = null;
		this.pendingTones[0] = 0.0;
		this.pendingTones[1] = 0.0;
		for (VesselTransshipmentOrder order : operations) {
			if (OperationType.LOAD.equals(order.getOpType())) {
				loadOperations.add(order);
				pendingTones[OperationType.LOAD.ordinal()] += order.getTones();
			}
			else {
				unloadOperations.add(order);
				pendingTones[OperationType.UNLOAD.ordinal()] += order.getTones();
			}
			if (order.getTones() > maxOp) {
				maxOp = order.getTones();
				major = order.getWares();
			}
		}
		this.initTones[OperationType.LOAD.ordinal()] = this.pendingTones[OperationType.LOAD.ordinal()];
		this.initTones[OperationType.UNLOAD.ordinal()] = this.pendingTones[OperationType.UNLOAD.ordinal()];
		return major;
	}
	
	/**
	 * @return the vesselId
	 */
	public int getVesselId() {
		return vesselId;
	}

	public ArrayList<VesselTransshipmentOrder> getLoadOperations() {
		return loadOperations;
	}

	public ArrayList<VesselTransshipmentOrder> getUnloadOperations() {
		return unloadOperations;
	}

	public WaresType getMainWares() {
		return mainWares;
	}
	
	public double getInitWorkload() {
		return initTones[0] + initTones[1];
	}

	/**
	 * @return the currentLoad
	 */
	public double getPendingWorkload() {
		return pendingTones[0] + pendingTones[1];
	}

	/**
	 * Returns true if the vessel includes at least one load operation which is not completely assigned 
	 * @return True if the vessel includes at least one load operation which is not completely assigned 
	 */
	public boolean includesLoadOperations() {
		for (VesselTransshipmentOrder order : loadOperations) {
			if (order.isPending())
				return true;
		}
		return false;
	}
	/**
	 * Books in advance a transshipment operation (either a load or unload). 
	 * This is useful to know whether a truck must return to continue performing operations.
	 * @param quantity Amount of load to move from vessel to truck or viceversa
	 * @return True if there is remaining load to work with; false otherwise
	 */
	public TransshipmentOrder getTransshipmentOrderForTruck(Truck truck) {
		// First try to assign an unload operation
		for (VesselTransshipmentOrder order : unloadOperations) {
			TransshipmentOrder orderForTruck = order.getTransshipmentOrderForTruck(truck);
			if (orderForTruck != null)
				return orderForTruck;
		}
		// If no pending unload operations, then go for a load operation
		for (VesselTransshipmentOrder order : loadOperations) {
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
		if (OperationType.UNLOAD.equals(truckOrder.getOpType()))
			for (VesselTransshipmentOrder order : unloadOperations) {
				if (order.performTransshipmentOrder(truckOrder)) {
					pendingTones[OperationType.UNLOAD.ordinal()] -= truckOrder.getTones();
					return true;
				}
			}
		else
			for (VesselTransshipmentOrder order : loadOperations) {
				if (order.performTransshipmentOrder(truckOrder)) {
					pendingTones[OperationType.LOAD.ordinal()] -= truckOrder.getTones();
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
		return (getPendingWorkload() < PortParkingModel.MIN_LOAD);
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
	 * TODO: Para elegir muelle, de momento, solo se tiene en cuenta su relación con la mercancía mayoritaria del barco, pero hay
	 * claras diferencias en los datos según el tipo de operación, también. 
	 * 
	 * @return
	 */
	public EnumSet<QuayType> getPotentialQuays() {
		return potentialQuays;
	}
}
