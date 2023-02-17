package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.location.Node;

// TODO: El tipo y cantidad de mercancía lo voy a definir por otro lado, y lo asignaré a cada tipo de barco
public enum VesselType {
	VTYPE1("Test Vessel Type 1", new Node("SOURCE_VESSEL_VTYPE1"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 100),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 1440), 0L),
	VTYPE2("Test Vessel Type 2", new Node("SOURCE_VESSEL_VTYPE2"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 150),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 220), 100L),
	VTYPE3("Test Vessel Type 3", new Node("SOURCE_VESSEL_VTYPE3"), 
			TimeFunctionFactory.getInstance("ConstantVariate", 500),
			new SimulationTimeFunction(PortParkingModel.TIME_UNIT, "ConstantVariate", 600), 200L);
	
	private final String description;
	private final Node initialLocation;
	private final TimeFunction timeToAnchorage;
	private final SimulationTimeFunction interarrivalTime; 
	private final long firstArrivalTime;
	
	/**
	 * @param description
	 * @param initialLocation
	 * @param timeToPortEntrance
	 */
	private VesselType(String description, Node initialLocation, TimeFunction timeToAnchorage, SimulationTimeFunction interarrivalTime, long firstArrivalTime) {
		this.description = description;
		this.initialLocation = initialLocation;
		this.timeToAnchorage = timeToAnchorage;
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
	public TimeFunction getTimeToAnchorage() {
		return timeToAnchorage;
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