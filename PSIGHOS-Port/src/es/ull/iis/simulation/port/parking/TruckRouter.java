/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Path;
import es.ull.iis.simulation.model.location.Router;

/**
 * @author Iván Castilla
 *
 */
public class TruckRouter implements Router {
	private final Node starting;
	private final Node ending;
	private final Node parking;
	private final ArrayList<Path> pathToParking;
	private final ArrayList<Path> pathFromParking;
	

	/**
	 * 
	 */
	public TruckRouter(int parkingCapacity, ArrayList<Double> pathTimes) {
		parking = new Node("Parking for trucks", parkingCapacity);
		starting = new Node("Starting spot for trucks");
		ending = new Node("Ending spot for trucks");
		pathToParking = new ArrayList<>();
		pathFromParking = new ArrayList<>();
		int pathCount = 0;
		for (double time : pathTimes) {
			pathToParking.add(new Path("Path " + pathCount, TimeFunctionFactory.getInstance("ConstantVariate", time), 1, 1));
			pathFromParking.add(0, new Path("Return path " + pathCount, TimeFunctionFactory.getInstance("ConstantVariate", time), 1, 1));
			pathCount++;
		}
		if (pathTimes.size() > 0) {
			starting.linkTo(pathToParking.get(0));
			pathToParking.get(pathCount - 1).linkTo(parking);
			parking.linkTo(pathFromParking.get(0));
			pathFromParking.get(pathCount - 1).linkTo(ending);
			for (int i = 1; i < pathTimes.size(); i++) {
				pathToParking.get(i - 1).linkTo(pathToParking.get(i));
				pathFromParking.get(i - 1).linkTo(pathFromParking.get(i));
			}
		}
	}

	/**
	 * @return the starting
	 */
	public Node getStarting() {
		return starting;
	}

	/**
	 * @return the ending
	 */
	public Node getEnding() {
		return ending;
	}

	/**
	 * @return the parking
	 */
	public Node getParking() {
		return parking;
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() > 0)
			return links.get(0);
		return Router.UNREACHABLE_LOCATION;
	}

}
