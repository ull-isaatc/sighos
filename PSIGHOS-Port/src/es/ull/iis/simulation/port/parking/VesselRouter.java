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
	private final Node starting;
	private final Node ending;
	private final Node[] quays;
	private final ArrayList<ArrayList<Path>> pathToQuay;
	private final ArrayList<ArrayList<Path>> pathFromQuay;
	

	/**
	 * 
	 */
	public VesselRouter(ArrayList<ArrayList<Double>> pathTimesPerQuay) {
		starting = new Node("Starting spot for vessels");
		ending = new Node("Ending spot for vessels");
		pathToQuay = new ArrayList<>();
		pathFromQuay = new ArrayList<>();
		quays = new Node[pathTimesPerQuay.size()];
		for (int i = 0; i < pathTimesPerQuay.size(); i++) {
			quays[i] = new Node("Quay " + i, Vessel.SIZE);
			final ArrayList<Path> pathsTo = new ArrayList<>();  
			final ArrayList<Path> pathsFrom = new ArrayList<>();
			final ArrayList<Double> pathTimes = pathTimesPerQuay.get(i);

			int pathCount = 0;
			final int totalPaths = pathTimes.size();
			for (double time : pathTimes) {
				pathsTo.add(new Path("TO_QUAY_" + i + "-Path " + pathCount + "/" + totalPaths, TimeFunctionFactory.getInstance("ConstantVariate", time), 1, 1));
				pathsFrom.add(0, new Path("FROM_QUAY_" + i + "-Path " + pathCount + "/" + totalPaths, TimeFunctionFactory.getInstance("ConstantVariate", time), 1, 1));
				pathCount++;
			}
			pathToQuay.add(pathsTo);
			pathFromQuay.add(pathsFrom);
			
			if (totalPaths > 0) {
				starting.linkTo(pathsTo.get(0));
				pathsTo.get(totalPaths - 1).linkTo(quays[i]);
				quays[i].linkTo(pathsFrom.get(0));
				pathsFrom.get(totalPaths - 1).linkTo(ending);
				for (int j = 1; j < totalPaths; j++) {
					pathsTo.get(j - 1).linkTo(pathsTo.get(j));
					pathsFrom.get(j - 1).linkTo(pathsFrom.get(j));
				}
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
	 * @return the specific quay
	 */
	public Node getQuay(int id) {
		return quays[id];
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location destination) {
		final ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() > 0)
			return links.get(0);
		return Router.UNREACHABLE_LOCATION;
	}

}
