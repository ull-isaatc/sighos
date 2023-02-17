package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.location.Node;

// TODO: Añadir una capacidad de 20 Toneladas += algo
public enum TruckCompany {
	TYPE1("Test Company 1", new Node("Location of company 1"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 10),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 10), 0L),
	TYPE2("Test Company 2", new Node("Location of company 2"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 15),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 10), 12L),
	TYPE3("Test Company 3", new Node("Location of company 3"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 5),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 10), 11L);
	
	private final String description;
	private final Node initialLocation;
	private final TimeFunction timeToPortEntrance;
	private final SimulationTimeFunction interarrivalTime; 
	private final long firstArrivalTime;
	
	/**
	 * @param description
	 * @param initialLocation
	 * @param timeToPortEntrance
	 */
	private TruckCompany(String description, Node initialLocation, TimeFunction timeToPortEntrance, SimulationTimeFunction interarrivalTime, long firstArrivalTime) {
		this.description = description;
		this.initialLocation = initialLocation;
		this.timeToPortEntrance = timeToPortEntrance;
		this.interarrivalTime = interarrivalTime;
		this.firstArrivalTime = firstArrivalTime;
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
	 * @return the interarrivalTime
	 */
	public SimulationTimeFunction getInterarrivalTime() {
		return interarrivalTime;
	}

	public long getFirstArrivalTime() {
		return firstArrivalTime;
	}
}