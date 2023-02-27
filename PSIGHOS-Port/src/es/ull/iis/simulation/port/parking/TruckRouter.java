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
		parking = new Node("PARKING", parkingCapacity);
		portEntrance = new Node("PORT_ENTRANCE");
		portExit = new Node("PORT_EXIT");
		final Path pathToParking = new Path("PATH_TO_PARKING", timeFromEntranceToParking);
		final Path pathFromParking = new Path("PATH_FROM_PARKING", timeFromParkingToExit);
		portEntrance.linkTo(pathToParking).linkTo(parking);
		parking.linkTo(pathFromParking).linkTo(portExit);
		for (TruckSource source : TruckSource.values()) {
			final Path pathToEntrance = new Path("PATH_TO_ENTRANCE_" + source.name(), source.getTimeToPortEntrance());
			source.getSpawnLocation().linkTo(pathToEntrance).linkTo(portEntrance);
			final Path pathFromExit = new Path("PATH_FROM_EXIT_" + source.name(), source.getTimeToPortEntrance());
			portExit.linkTo(pathFromExit).linkTo(source.getSpawnLocation());
			final Path pathToWarehouse = new Path("PATH_TO_WAREHOUSE", source.getTimeToWarehouse());
			final Path pathFromWarehouse = new Path("PATH_FROM_WAREHOUSE", source.getTimeToWarehouse());
			source.getSpawnLocation().linkTo(pathToWarehouse).linkTo(source.getWarehouseLocation());
			source.getWarehouseLocation().linkTo(pathFromWarehouse).linkTo(source.getSpawnLocation());
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
			if (!portEntrance.equals(destination)) {
				for (TruckSource source : TruckSource.values()) {
					if (source.getWarehouseLocation().equals(destination)) {
						return links.get(1);
					}
					if (source.getSpawnLocation().equals(destination)) {
						return links.get(source.ordinal());
					}
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
