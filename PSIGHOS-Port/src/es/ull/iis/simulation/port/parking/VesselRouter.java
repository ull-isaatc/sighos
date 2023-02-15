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
public class VesselRouter implements Router {
	private final Node anchorage;
	private final Node[] quays;
	private final Path[] pathToQuay;
	private final Path[] pathFromQuay;
	

	/**
	 * 
	 */
	public VesselRouter(TimeFunction []pathTimesPerQuay) {
		anchorage = new Node("ANCHORAGE");
		pathToQuay = new Path[pathTimesPerQuay.length];
		pathFromQuay = new Path[pathTimesPerQuay.length];
		quays = new Node[pathTimesPerQuay.length];
		for (int i = 0; i < pathTimesPerQuay.length; i++) {
			quays[i] = new Node("QUAY_" + i, Vessel.SIZE);
			final Path pathTo = new Path("PATH_TO_QUAY_" + i, pathTimesPerQuay[i], 1, 1);  
			final Path pathFrom = new Path("PATH_FROM_QUAY_" + i, pathTimesPerQuay[i], 1, 1);
			pathToQuay[i] = pathTo;
			pathFromQuay[i] = pathFrom;
			anchorage.linkTo(pathTo).linkTo(quays[i]);
			quays[i].linkTo(pathFrom).linkTo(anchorage);
		}
		for (VesselType type: VesselType.values()) {
			final Path pathTo = new Path("PATH_TO_ANCHORAGE_" + type, type.getTimeToAnchorage(), 1, 1);  
			final Path pathFrom = new Path("PATH_FROM_ANCHORAGE_" + type, type.getTimeToAnchorage(), 1, 1);  
			type.getInitialLocation().linkTo(pathTo).linkTo(anchorage);
			anchorage.linkTo(pathFrom).linkTo(type.getInitialLocation());
		}
	}

	/**
	 * @return the starting
	 */
	public Node getAnchorage() {
		return anchorage;
	}

	/**
	 * @return the specific quay
	 */
	public Node getQuay(int id) {
		return quays[id];
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() == 1) {
			return links.get(0);
		}
		else if (links.size() > 1) {
			for (int i = 0; i < quays.length; i++) {
				if (quays[i].equals(destination)) {
					return links.get(i);
				}
			}
			for (int i = 0; i < VesselType.values().length; i++) {
				if (VesselType.values()[i].getInitialLocation().equals(destination)) {
					return links.get(i + quays.length);
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
