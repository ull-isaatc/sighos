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
		VESSEL_LOADED("VESSEL LOADED"),
		VESSEL_UNLOADED("VESSEL UNLOADED");
		
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
	public PortInfo(Simulation simul, Type type, Vessel vessel, TransshipmentOrder order, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = null;
		this.vessel = vessel;
		this.order = order;
	}
	
	public PortInfo(Simulation simul, Vessel vessel, long ts) {
		super(simul, ts);
		this.type = Type.VESSEL_CREATED;
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
			msg += truck.toString() + "\t" + type.getDescription() + "\t" + order.getWares().getDescription() + "\t" + String.format(Locale.US, "%.2f", order.getTones());
			break;
		case VESSEL_LOADED:
		case VESSEL_UNLOADED:
			msg += vessel.toString() + "\t" + type.getDescription() + "\t" + order.getWares().getDescription() + "\t" + String.format(Locale.US, "%.2f", order.getTones());
//			msg += vessel.toString() + "\t" + type.getDescription() + "\t" + String.format(Locale.US, "%.2f", vessel.getPendingWorkload()) + "/" + String.format(Locale.US, "%.2f", vessel.getInitWorkload());
			break;
		case VESSEL_CREATED:
			final String baseMsg = msg + vessel.toString() + "\t";
			msg += vessel.toString() + "\t" + type.getDescription() + System.lineSeparator();
			for (VesselTransshipmentOrder vOrder : vessel.getUnloadOperations()) {
				msg += baseMsg + "WITH INITIAL " + vOrder.getOpType() + "\t" + vOrder.getWares().getDescription() + "\t" + vOrder.getTones() + System.lineSeparator();
			}
			for (VesselTransshipmentOrder vOrder : vessel.getLoadOperations()) {
				msg += baseMsg + "WITH INITIAL " + vOrder.getOpType() + "\t" + vOrder.getWares().getDescription() + "\t" + vOrder.getTones() + System.lineSeparator();
			}
			break;			
		default:
			break;
		}		
		return msg;
	}
}

