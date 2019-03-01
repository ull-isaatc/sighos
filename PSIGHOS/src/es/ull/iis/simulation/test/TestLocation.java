/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.info.ElementLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Path;
import es.ull.iis.simulation.model.location.RouteFlow;
import es.ull.iis.simulation.model.location.Router;
import es.ull.iis.simulation.model.location.TimeDrivenMovableElementGenerator;

class ExpLocation extends Experiment {
	private final long endTs;

	public ExpLocation(int nExperiments, long endTs) {
		super("Experiment with locations", nExperiments);
		this.endTs = endTs;
	}

	@Override
	public Simulation getSimulation(int ind) {
		final SimulLocation sim =  new SimulLocation(ind, endTs);
		sim.addInfoReceiver(new LocationListener());
		return sim;
	}
	
}

class MyRouter implements Router {
	private final static int NPATHS = 3;
	final private Node home;
	final private Node destination;
	final private Path[] paths;
	
	public MyRouter(int nElems) {
		home = new Node("Home", TimeFunctionFactory.getInstance("ConstantVariate", 5), nElems);
		paths = new Path[NPATHS];
		for (int i = 0; i < NPATHS; i++) {
			paths[i] = new Path("Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", 10), 1, 2);
		}
		destination = new Node("Destination", TimeFunctionFactory.getInstance("ConstantVariate", 0), nElems);

		home.linkTo(paths[0]);
		for (int i = 0; i < NPATHS - 1; i++) {
			paths[i].linkTo(paths[i + 1]);
		}
		paths[NPATHS - 1].linkTo(destination);
	}
	
	
	/**
	 * @return the home
	 */
	public Node getHome() {
		return home;
	}


	/**
	 * @return the destination
	 */
	public Node getDestination() {
		return destination;
	}

	@Override
	public Location getNextLocationTo(Movable entity, Location finalLocation) {
		ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() > 0)
			return links.get(0);
		return null;
	}
	
}

class MyNoSizeRouter implements Router {
	private final static int NPATHS = 3;
	final private Node home;
	final private Node destination;
	final private Path[] paths;
	
	public MyNoSizeRouter(int nElems) {
		home = new Node("Home", TimeFunctionFactory.getInstance("ConstantVariate", 5));
		paths = new Path[NPATHS];
		for (int i = 0; i < NPATHS; i++) {
			paths[i] = new Path("Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", 10));
		}
		destination = new Node("Destination", TimeFunctionFactory.getInstance("ConstantVariate", 0));

		home.linkTo(paths[0]);
		for (int i = 0; i < NPATHS - 1; i++) {
			paths[i].linkTo(paths[i + 1]);
		}
		paths[NPATHS - 1].linkTo(destination);
	}
	
	
	/**
	 * @return the home
	 */
	public Node getHome() {
		return home;
	}


	/**
	 * @return the destination
	 */
	public Node getDestination() {
		return destination;
	}


	@Override
	public Location getNextLocationTo(Movable entity, Location finalLocation) {
		ArrayList<Location> links = entity.getLocation().getLinkedTo();
		if (links.size() > 0)
			return links.get(0);
		return null;
	}
	
}


class SimulLocation extends Simulation {
	private final static int NELEM = 3;
	
	public SimulLocation(int id, long endTs) {
		super(id, "Simulating locations " + id, 0, endTs);
//		final MyNoSizeRouter router = new MyNoSizeRouter(NELEM); 
		final MyRouter router = new MyRouter(NELEM); 
		final RouteFlow initFlow = new RouteFlow(this, "From home to destination", router.getDestination(), router);
		final ElementType et = new ElementType(this, "Car");
		new TimeDrivenMovableElementGenerator(this, NELEM, et, initFlow, 1, router.getHome(), new SimulationPeriodicCycle(getTimeUnit(), 0L, new SimulationTimeFunction(getTimeUnit(), "ConstantVariate", getEndTs()), 1));
	}
	
}

class LocationListener extends Listener {

	public LocationListener() {
		super("Location listener");
		addGenerated(ElementLocationInfo.class);
		addEntrance(ElementLocationInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		final ElementLocationInfo eInfo = (ElementLocationInfo)info;
		System.out.println(eInfo);
	}
	
}
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestLocation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpLocation(1, 100).start();;

	}

}
