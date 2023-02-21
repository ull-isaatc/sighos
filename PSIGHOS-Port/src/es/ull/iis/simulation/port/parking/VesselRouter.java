/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.ArrayList;

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
	private final Node anchorage;
	private final int nQuays;

	/**
	 * 
	 */
	public VesselRouter(Simulation model) {
		anchorage = new Node("ANCHORAGE");
		nQuays = QuayType.values().length;
		for (QuayType quay : QuayType.values()) {
			final Path pathTo = new Path("PATH_TO_QUAY_" + quay, quay.getTimeFromAnchorage(), 1, 1);  
			final Path pathFrom = new Path("PATH_FROM_QUAY_" + quay, quay.getTimeFromAnchorage(), 1, 1);
			anchorage.linkTo(pathTo).linkTo(quay.getLocation());
			quay.getLocation().linkTo(pathFrom).linkTo(anchorage);
		}
		for (VesselSource source : VesselSource.values()) {
			final Path pathTo = new Path("PATH_TO_ANCHORAGE_FROM_" + source, source.getTimeToAnchorage(), 1, 1);  
			final Path pathFrom = new Path("PATH_FROM_ANCHORAGE_TO_" + source, source.getTimeToAnchorage(), 1, 1);
			source.getInitialLocation().linkTo(pathTo).linkTo(anchorage);
			anchorage.linkTo(pathFrom).linkTo(source.getInitialLocation());
		}
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
				if (QuayType.values()[i].getLocation().equals(destination)) {
					return links.get(i);
				}
			}
			for (VesselSource source : VesselSource.values()) {
				if (source.getInitialLocation().equals(destination)) {
					return links.get(source.ordinal() + nQuays);
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
