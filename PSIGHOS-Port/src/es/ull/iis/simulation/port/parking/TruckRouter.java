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
	private final Node waitingArea;
	private final Node portExit;
	private final Node transshipmentArea;
	

	/**
	 * 
	 */
	public TruckRouter(int parkingCapacity, TimeFunction timeFromWaitingToTransshipmentArea, TimeFunction timeFromTransshipmentToExitArea) {
		transshipmentArea = Locations.TRUCK_TRANSSHIPMENT_AREA.getNode();
		waitingArea = Locations.TRUCK_WAIT_AREA.getNode();
		portExit = Locations.TRUCK_EXIT_POINT.getNode();
		final Path pathToParking = new Path("PATH_TO_TRANSSHIPMENT_AREA", timeFromWaitingToTransshipmentArea);
		final Path pathFromParking = new Path("PATH_FROM_TRANSSHIPMENT_AREA", timeFromTransshipmentToExitArea);
		waitingArea.linkTo(pathToParking).linkTo(transshipmentArea);
		transshipmentArea.linkTo(pathFromParking).linkTo(portExit);
		for (TruckSource source : TruckSource.values()) {
			final Path pathToEntrance = new Path("PATH_TO_WAITING_AREA_" + source.name(), source.getTimeToWaitingArea());
			source.getSpawnLocation().getNode().linkTo(pathToEntrance).linkTo(waitingArea);
			final Path pathFromExit = new Path("PATH_FROM_WAITING_AREA_" + source.name(), source.getTimeToWaitingArea());
			portExit.linkTo(pathFromExit).linkTo(source.getSpawnLocation().getNode());
			final Path pathToWarehouse = new Path("PATH_TO_WAREHOUSE", source.getTimeToWarehouse());
			final Path pathFromWarehouse = new Path("PATH_FROM_WAREHOUSE", source.getTimeToWarehouse());
			source.getSpawnLocation().getNode().linkTo(pathToWarehouse).linkTo(source.getWarehouseLocation());
			source.getWarehouseLocation().linkTo(pathFromWarehouse).linkTo(source.getSpawnLocation().getNode());
		}
	}

	/**
	 * @return the starting
	 */
	public Node getWaitingArea() {
		return waitingArea;
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
	public Node getTransshipmentArea() {
		return transshipmentArea;
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() == 1) {
			return links.get(0);
		}
		else if (links.size() > 1) {
			if (!waitingArea.equals(destination)) {
				for (TruckSource source : TruckSource.values()) {
					if (source.getWarehouseLocation().equals(destination)) {
						return links.get(1);
					}
					if (source.getSpawnLocation().getNode().equals(destination)) {
						return links.get(source.ordinal());
					}
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
