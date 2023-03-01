/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Path;
import es.ull.iis.simulation.model.location.Router;

/**
 * @author Iván Castilla
 *
 */
public class VesselRouter implements Router {
	private final Node initialLocation;
	private final Node anchorage;
	private final int nQuays;

	/**
	 * 
	 */
	public VesselRouter(Simulation model, TimeFunction fromSourceToAnchorage) {
		initialLocation = Locations.VESSEL_SRC.getNode();
		anchorage = Locations.VESSEL_ANCHORAGE.getNode();
		Path pathTo = new Path("PATH_TO_ANCHORAGE_FROM_SOURCE", fromSourceToAnchorage, 1, 1);  
		Path pathFrom = new Path("PATH_FROM_ANCHORAGE_TO_SOURCE", fromSourceToAnchorage, 1, 1);
		initialLocation.linkTo(pathTo).linkTo(anchorage);
		anchorage.linkTo(pathFrom).linkTo(initialLocation);
		nQuays = QuayType.values().length;
		for (QuayType quay : QuayType.values()) {
			pathTo = new Path("PATH_TO_" + quay, quay.getTimeFromAnchorage(), 1, 1);  
			pathFrom = new Path("PATH_FROM_" + quay, quay.getTimeFromAnchorage(), 1, 1);
			anchorage.linkTo(pathTo).linkTo(quay.getLocation().getNode());
			quay.getLocation().getNode().linkTo(pathFrom).linkTo(anchorage);
		}
	}

	/**
	 * @return the initialLocation
	 */
	public Node getInitialLocation() {
		return initialLocation;
	}

	/**
	 * @return the starting
	 */
	public Node getAnchorage() {
		return anchorage;
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() == 1) {
			return links.get(0);
		}
		else if (links.size() > 1) {
			for (int i = 0; i < nQuays; i++) {
				if (QuayType.values()[i].getLocation().getNode().equals(destination)) {
					return links.get(i + 1);
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
