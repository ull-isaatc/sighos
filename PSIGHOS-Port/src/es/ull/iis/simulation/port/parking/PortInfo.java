/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.Locale;

import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author masbe
 *
 */
public class PortInfo extends AsynchronousInfo {
	public enum Type {
		VESSEL_CREATED("VESSEL CREATED"),
		TRUCK_LOADED("TRUCK LOADED"),
		TRUCK_UNLOADED("TRUCK UNLOADED"),
		VESSEL_LOADED("VESSEL UNLOADED"),
		VESSEL_UNLOADED("VESSEL LOADED");
		
		private final String description;
		
		private Type(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}
	}

	private final Type type;
	private final Truck truck; 
	private final Vessel vessel;
	private final TransshipmentOrder order;
	
	/**
	 * @param simul
	 */
	public PortInfo(Simulation simul, Type type, Truck truck, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = truck;
		this.vessel = null;
		this.order = truck.getOrder();
	}
	
	/**
	 * @param simul
	 */
	public PortInfo(Simulation simul, Type type, Vessel vessel, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = null;
		this.vessel = vessel;
		this.order = null;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the truck
	 */
	public Truck getTruck() {
		return truck;
	}

	/**
	 * @return the vessel
	 */
	public Vessel getVessel() {
		return vessel;
	}

	@Override
	public String toString() {
		String msg = "" + simul.long2SimulationTime(getTs()) + "\t";
		switch (type) {
		case TRUCK_LOADED:
		case TRUCK_UNLOADED:
			msg += truck.toString() + "\t" + type.getDescription() + "\t" + String.format(Locale.US, "%.2f", order.getTones()) + "/" + String.format(Locale.US, "%.2f", truck.getMaxLoad()) + 
					"\t" + order.getWares().getDescription();
			break;
		case VESSEL_LOADED:
		case VESSEL_UNLOADED:
			msg += vessel.toString() + "\t" + type.getDescription() + "\t" + String.format(Locale.US, "%.2f", vessel.getPendingWorkload()) + "/" + String.format(Locale.US, "%.2f", vessel.getInitWorkload());
			break;
		case VESSEL_CREATED:
			msg += vessel.toString() + "\t" + type.getDescription() + "\t";
			for (VesselTransshipmentOrder vOrder : vessel.getUnloadOperations()) {
				msg += vOrder + " ";
			}
			for (VesselTransshipmentOrder vOrder : vessel.getLoadOperations()) {
				msg += vOrder + " ";
			}
			break;			
		default:
			break;
		}		
		return msg;
	}
}

