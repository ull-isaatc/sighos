package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Node;

public enum VesselSource {
	VSOURCE1("Vessel Source 1", new Node("VESSEL_SOURCE_1"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 100), 0.5),
	VSOURCE2("Vessel Source 2", new Node("VESSEL_SOURCE_2"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 150), 0.3),
	VSOURCE3("Vessel Source 3", new Node("VESSEL_SOURCE_3"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 500), 0.2);
	
	private final String description;
	private final Node initialLocation;
	private final TimeFunction timeToAnchorage;
	private final double proportion;
	
	/**
	 * @param description
	 * @param initialLocation
	 * @param timeToPortEntrance
	 */
	private VesselSource(String description, Node initialLocation, TimeFunction timeToAnchorage, double proportion) {
		this.description = description;
		this.initialLocation = initialLocation;
		this.timeToAnchorage = timeToAnchorage;
		this.proportion = proportion;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the initialLocation
	 */
	public Node getInitialLocation() {
		return initialLocation;
	}

	/**
	 * @return the timeToPortEntrance
	 */
	public TimeFunction getTimeToAnchorage() {
		return timeToAnchorage;
	}

	public double getProportion() {
		return proportion;
	}
}