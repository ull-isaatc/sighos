package es.ull.iis.simulation.port.parking;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Node;

public enum TruckSource {
	TYPE1(Locations.TRUCK_SRC_SOUTH1, new Node("WAREHOUSE#1"),
			TimeFunctionFactory.getInstance("UniformVariate", 10, 15),
			TimeFunctionFactory.getInstance("ConstantVariate", 10), 
			TimeFunctionFactory.getInstance("UniformVariate", 30, 60), 0.2),
	TYPE2(Locations.TRUCK_SRC_SOUTH2, new Node("WAREHOUSE#2"), 
			TimeFunctionFactory.getInstance("UniformVariate", 15, 20), 
			TimeFunctionFactory.getInstance("ConstantVariate", 15), 
			TimeFunctionFactory.getInstance("UniformVariate", 60, 90), 0.3),
	TYPE3(Locations.TRUCK_SRC_SOUTH3, new Node("WAREHOUSE#3"), 
			TimeFunctionFactory.getInstance("UniformVariate", 5, 10), 
			TimeFunctionFactory.getInstance("ConstantVariate", 5), 
			TimeFunctionFactory.getInstance("UniformVariate", 50, 70), 0.5),
	TYPE4(Locations.TRUCK_SRC_NORTH1, new Node("WAREHOUSE#4"), 
			TimeFunctionFactory.getInstance("UniformVariate", 5, 10), 
			TimeFunctionFactory.getInstance("ConstantVariate", 5), 
			TimeFunctionFactory.getInstance("UniformVariate", 50, 70), 0.5);
	
	private final Locations spawnLocation;
	private final Node warehouseLocation;
	private final TimeFunction initialDelay;
	private final TimeFunction timeToPortEntrance;
	private final TimeFunction timeToWarehouse;
	private final double proportion;
	
	/**
	 * @param description
	 * @param spawnLocation
	 * @param timeToPortEntrance
	 */
	private TruckSource(Locations spawnLocation, Node warehouseLocation, TimeFunction initialDelay, TimeFunction timeToPortEntrance, TimeFunction timeToWarehouseAndBack, double proportion) {
		this.spawnLocation = spawnLocation;
		this.warehouseLocation = warehouseLocation;
		this.initialDelay = initialDelay;
		this.timeToPortEntrance = timeToPortEntrance;
		this.timeToWarehouse = timeToWarehouseAndBack;
		this.proportion = proportion;
	}

	/**
	 * @return the initialLocation
	 */
	public Locations getSpawnLocation() {
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

	/**
	 * @return the proportion
	 */
	public double getProportion() {
		return proportion;
	}
}