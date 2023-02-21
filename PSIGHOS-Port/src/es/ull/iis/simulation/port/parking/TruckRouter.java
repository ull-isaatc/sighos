/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
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
	private final Node portEntrance;
	private final Node portExit;
	private final Node parking;
	

	/**
	 * 
	 */
	public TruckRouter(int parkingCapacity, TimeFunction timeFromEntranceToParking, TimeFunction timeFromParkingToExit) {
		parking = new Node("Parking for trucks", parkingCapacity);
		portEntrance = new Node("Port entrance");
		portExit = new Node("Port exit");
		final Path pathToParking = new Path("Path to parking", timeFromEntranceToParking);
		final Path pathFromParking = new Path("Path from parking", timeFromParkingToExit);
		portEntrance.linkTo(pathToParking).linkTo(parking);
		parking.linkTo(pathFromParking).linkTo(portExit);
		for (TruckSource source : TruckSource.values()) {
			final Path pathToEntrance = new Path("Path to entrance " + source.name(), source.getTimeToPortEntrance());
			source.getInitialLocation().linkTo(pathToEntrance).linkTo(portEntrance);
			final Path pathFromExit = new Path("Path from exit " + source.name(), source.getTimeToPortEntrance());
			portExit.linkTo(pathFromExit).linkTo(source.getInitialLocation());
		}
	}

	/**
	 * @return the starting
	 */
	public Node getPortEntrance() {
		return portEntrance;
	}

	/**
	 * @return the ending
	 */
	public Node getPortExit() {
		return portExit;
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
		if (links.size() == 1) {
			return links.get(0);
		}
		else if (links.size() > 1) {
			for (TruckSource source : TruckSource.values()) {
				if (source.getInitialLocation().equals(destination))
					return links.get(source.ordinal());
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
