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
		TRUCK_LOADED("TRUCK LOADED"),
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
	
	/**
	 * @param simul
	 */
	public PortInfo(Simulation simul, Type type, Truck truck, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = truck;
		this.vessel = null;
	}
	
	/**
	 * @param simul
	 */
	public PortInfo(Simulation simul, Type type, Vessel vessel, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = null;
		this.vessel = vessel;
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
			msg += truck.toString() + "\t" + type.getDescription() + "\t" + String.format(Locale.US, "%.2f", truck.getCurrentLoad()) + "/" + String.format(Locale.US, "%.2f", truck.getMaxLoad());
			break;
		case VESSEL_UNLOADED:
			msg += vessel.toString() + "\t" + type.getDescription() + "\t" + String.format(Locale.US, "%.2f", vessel.getCurrentLoad()) + "/" + String.format(Locale.US, "%.2f", vessel.getInitLoad());
			break;
		default:
			break;
		}		
		return msg;
	}
}

