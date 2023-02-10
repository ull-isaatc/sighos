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
		for (TruckCompany co : TruckCompany.values()) {
			final Path pathToEntrance = new Path("Path to entrance " + co.name(), co.getTimeToPortEntrance());
			co.getInitialLocation().linkTo(pathToEntrance).linkTo(portEntrance);
			portExit.linkTo(co.getInitialLocation());
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
		if (links.size() > 0)
			return links.get(0);
		return Router.UNREACHABLE_LOCATION;
	}

}
