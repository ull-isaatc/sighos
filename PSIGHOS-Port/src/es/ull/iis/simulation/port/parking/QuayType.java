/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;

/**
 * @author Iván Castilla Rodríguez
 */
public enum QuayType {
	QUAY1(Locations.VESSEL_QUAY1, 2, TimeFunctionFactory.getInstance("ConstantVariate", 50)),
	QUAY2(Locations.VESSEL_QUAY2, 1, TimeFunctionFactory.getInstance("ConstantVariate", 60)),
	QUAY3(Locations.VESSEL_QUAY3, 1, TimeFunctionFactory.getInstance("ConstantVariate", 70)),
	QUAY4(Locations.VESSEL_QUAY4, 1, TimeFunctionFactory.getInstance("ConstantVariate", 80)),
	QUAY5(Locations.VESSEL_QUAY5, 2, TimeFunctionFactory.getInstance("ConstantVariate", 50)),
	QUAY6(Locations.VESSEL_QUAY6, 1, TimeFunctionFactory.getInstance("ConstantVariate", 60)),
	QUAY7(Locations.VESSEL_QUAY7, 1, TimeFunctionFactory.getInstance("ConstantVariate", 70)),
	QUAY8(Locations.VESSEL_QUAY8, 1, TimeFunctionFactory.getInstance("ConstantVariate", 70)),
	QUAY9(Locations.VESSEL_QUAY9, 1, TimeFunctionFactory.getInstance("ConstantVariate", 80));
	
	private final Locations location;	
	private final TimeFunction timeFromAnchorage;
	private final int capacity;

	private QuayType(Locations location, int capacity, TimeFunction timeFromAnchorage) {
		this.location = location;
		this.capacity = capacity;
		this.timeFromAnchorage = timeFromAnchorage;
	}
	
	/**
	 * @return the capacity
	 */
	public int getCapacity() {
		return capacity;
	}

	public Locations getLocation() {
		return location;
	}

	public TimeFunction getTimeFromAnchorage() {
		return timeFromAnchorage;
	}
}
