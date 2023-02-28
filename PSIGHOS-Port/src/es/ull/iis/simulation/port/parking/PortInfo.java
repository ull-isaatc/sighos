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
		TRUCK_LOADED("TRUCK LOADED");
		
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
	/**
	 * @param simul
	 */
	public PortInfo(Simulation simul, Type type, Truck truck, long ts) {
		super(simul, ts);
		this.type = type;
		this.truck = truck;
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

	@Override
	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + truck.toString() + "\t" + type.getDescription() + (Type.TRUCK_LOADED.equals(type) ? 
				"\t" + String.format(Locale.US, "%.2f", truck.getCurrentLoad()) + "/" + String.format(Locale.US, "%.2f", truck.getServingVessel().getCurrentLoad()) : "");
	}
}

