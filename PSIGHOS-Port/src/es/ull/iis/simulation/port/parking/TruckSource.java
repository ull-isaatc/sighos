package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Node;

public enum TruckSource {
	TYPE1("Test Source 1", new Node("Location of source 1"),
			TimeFunctionFactory.getInstance("UniformVariate", 10, 15),
			TimeFunctionFactory.getInstance("ConstantVariate", 10), 
			TimeFunctionFactory.getInstance("UniformVariate", 30, 60)),
	TYPE2("Test Source 2", new Node("Location of source 2"), 
			TimeFunctionFactory.getInstance("UniformVariate", 15, 20), 
			TimeFunctionFactory.getInstance("ConstantVariate", 15), 
			TimeFunctionFactory.getInstance("UniformVariate", 120, 180)),
	TYPE3("Test Source 3", new Node("Location of source 3"), 
			TimeFunctionFactory.getInstance("UniformVariate", 5, 10), 
			TimeFunctionFactory.getInstance("ConstantVariate", 5), 
			TimeFunctionFactory.getInstance("UniformVariate", 60, 120));
	
	private final String description;
	private final Node initialLocation;
	private final TimeFunction timeToInitialLocation;
	private final TimeFunction timeToPortEntrance;
	private final TimeFunction timeToDeliverAndBack;
	
	/**
	 * @param description
	 * @param initialLocation
	 * @param timeToPortEntrance
	 */
	private TruckSource(String description, Node initialLocation, TimeFunction timeToInitialLocation, TimeFunction timeToPortEntrance, TimeFunction timeToDeliverAndBack) {
		this.description = description;
		this.initialLocation = initialLocation;
		this.timeToInitialLocation = timeToInitialLocation;
		this.timeToPortEntrance = timeToPortEntrance;
		this.timeToDeliverAndBack = timeToDeliverAndBack;
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
	public TimeFunction getTimeToPortEntrance() {
		return timeToPortEntrance;
	}

	/**
	 * @return the timeToInitialLocation
	 */
	public TimeFunction getTimeToInitialLocation() {
		return timeToInitialLocation;
	}

	/**
	 * @return the timeToDeliverAndBack
	 */
	public TimeFunction getTimeToDeliverAndBack() {
		return timeToDeliverAndBack;
	}
}