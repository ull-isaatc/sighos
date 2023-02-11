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
public class VesselRouter implements Router {
	private final Node anchorage;
	private final Node[] quays;
	private final Path[] pathToQuay;
	private final Path[] pathFromQuay;
	

	/**
	 * 
	 */
	public VesselRouter(ArrayList<Double> pathTimesPerQuay) {
		anchorage = new Node("Anchorage for vessels");
		pathToQuay = new Path[pathTimesPerQuay.size()];
		pathFromQuay = new Path[pathTimesPerQuay.size()];
		quays = new Node[pathTimesPerQuay.size()];
		for (int i = 0; i < pathTimesPerQuay.size(); i++) {
			quays[i] = new Node("Quay " + i, Vessel.SIZE);
			final Path pathTo = new Path("TO_QUAY_" + i + "-Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", pathTimesPerQuay.get(i)), 1, 1);  
			final Path pathFrom = new Path("FROM_QUAY_" + i + "-Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", pathTimesPerQuay.get(i)), 1, 1);
			pathToQuay[i] = pathTo;
			pathFromQuay[i] = pathFrom;
			anchorage.linkTo(pathTo).linkTo(quays[i]);
			quays[i].linkTo(pathFrom).linkTo(anchorage);
		}
		for (VesselType type: VesselType.values()) {
			type.getInitialLocation().linkTo(anchorage);
			anchorage.linkTo(type.getInitialLocation());
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
		if (links.size() > 0) {
			for (int i = 0; i < quays.length; i++) {
				if (quays[i].equals(destination)) {
					
				}
			}
			return links.get(0);
		}
		return Router.UNREACHABLE_LOCATION;
	}

}
