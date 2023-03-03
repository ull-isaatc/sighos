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
	RAOS1(Locations.VESSEL_QUAY_RAOS1, 2, TimeFunctionFactory.getInstance("ConstantVariate", 50)),
	RAOS2(Locations.VESSEL_QUAY_RAOS2, 1, TimeFunctionFactory.getInstance("ConstantVariate", 60)),
	RAOS3(Locations.VESSEL_QUAY_RAOS3, 1, TimeFunctionFactory.getInstance("ConstantVariate", 70)),
	RAOS4(Locations.VESSEL_QUAY_RAOS4, 1, TimeFunctionFactory.getInstance("ConstantVariate", 80)),
	RAOS5(Locations.VESSEL_QUAY_RAOS5, 2, TimeFunctionFactory.getInstance("ConstantVariate", 50)),
	NMONTANA(Locations.VESSEL_QUAY_NMONTANA, 1, TimeFunctionFactory.getInstance("ConstantVariate", 60));
	
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
