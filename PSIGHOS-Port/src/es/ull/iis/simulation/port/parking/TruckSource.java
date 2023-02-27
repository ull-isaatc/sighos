package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Node;

public enum TruckSource {
	TYPE1("Test Source 1", new Node("TRUCK_SPAWN#1"), new Node("WAREHOUSE#1"),
			TimeFunctionFactory.getInstance("UniformVariate", 10, 15),
			TimeFunctionFactory.getInstance("ConstantVariate", 10), 
			TimeFunctionFactory.getInstance("UniformVariate", 30, 60)),
	TYPE2("Test Source 2", new Node("TRUCK_SPAWN#2"), new Node("WAREHOUSE#2"), 
			TimeFunctionFactory.getInstance("UniformVariate", 15, 20), 
			TimeFunctionFactory.getInstance("ConstantVariate", 15), 
			TimeFunctionFactory.getInstance("UniformVariate", 60, 90)),
	TYPE3("Test Source 3", new Node("TRUCK_SPAWN#3"), new Node("WAREHOUSE#3"), 
			TimeFunctionFactory.getInstance("UniformVariate", 5, 10), 
			TimeFunctionFactory.getInstance("ConstantVariate", 5), 
			TimeFunctionFactory.getInstance("UniformVariate", 50, 70));
	
	private final String description;
	private final Node spawnLocation;
	private final Node warehouseLocation;
	private final TimeFunction initialDelay;
	private final TimeFunction timeToPortEntrance;
	private final TimeFunction timeToWarehouse;
	
	/**
	 * @param description
	 * @param spawnLocation
	 * @param timeToPortEntrance
	 */
	private TruckSource(String description, Node spawnLocation, Node warehouseLocation, TimeFunction initialDelay, TimeFunction timeToPortEntrance, TimeFunction timeToWarehouseAndBack) {
		this.description = description;
		this.spawnLocation = spawnLocation;
		this.warehouseLocation = warehouseLocation;
		this.initialDelay = initialDelay;
		this.timeToPortEntrance = timeToPortEntrance;
		this.timeToWarehouse = timeToWarehouseAndBack;
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
	public Node getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * @return the warehouseLocation
	 */
	public Node getWarehouseLocation() {
		return warehouseLocation;
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
	public TimeFunction getInitialDelay() {
		return initialDelay;
	}

	/**
	 * @return the timeToDeliverAndBack
	 */
	public TimeFunction getTimeToWarehouse() {
		return timeToWarehouse;
	}
}