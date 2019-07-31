/**
 * 
 */
package es.ull.iis.simulation.test.location;

import java.util.ArrayList;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.EntityLocationInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.location.Location;
import es.ull.iis.simulation.model.location.Movable;
import es.ull.iis.simulation.model.location.MoveResourcesFlow;
import es.ull.iis.simulation.model.location.Node;
import es.ull.iis.simulation.model.location.Path;
import es.ull.iis.simulation.model.location.Router;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestResourcesLocation extends Experiment {
	private static final long ENDTS = 200;
	private static final int NELEM = 3;
	private static final int NTRUCKS = 2;
	private static final int TRUCKSIZE = 1;
	private static final int NEXP = 1;
	private static final int NPATHS = 3;
	private static final long DELAY_HOME = 5;
	private static final long DELAY_PATH = 10;
	private static final boolean NOSIZE = false;
	private static final boolean UNREACHABLE = false;

	public TestResourcesLocation() {
		super("Experiment with locations", NEXP);
	}

	class MyRouter implements Router {
		final private Node home;
		final private Node destination;
		final private Path[] paths;
		
		public MyRouter() {
			home = NOSIZE ? new Node("Home", TimeFunctionFactory.getInstance("ConstantVariate", DELAY_HOME)) :
				new Node("Home", TimeFunctionFactory.getInstance("ConstantVariate", DELAY_HOME), NELEM * TRUCKSIZE);
			paths = new Path[NPATHS];
			for (int i = 0; i < NPATHS; i++) {
				paths[i] = NOSIZE ? new Path("Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", DELAY_PATH)) :
					new Path("Path " + i, TimeFunctionFactory.getInstance("ConstantVariate", DELAY_PATH), 1, 1);
			}
			destination = NOSIZE ? new Node("Destination", TimeFunctionFactory.getInstance("ConstantVariate", 0)) :
				new Node("Destination", NELEM * TRUCKSIZE);

			home.linkTo(paths[0]);
			for (int i = 0; i < NPATHS - 1; i++) {
				paths[i].linkTo(paths[i + 1]);
			}
			if (!UNREACHABLE)
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
			if (destination.equals(finalLocation)) {
				final ArrayList<Location> links = entity.getLocation().getLinkedTo();
				if (links.size() > 0)
					return links.get(0);
			}
			else if (home.equals(finalLocation)) {
				final ArrayList<Location> links = entity.getLocation().getLinkedFrom();
				if (links.size() > 0)
					return links.get(0);
			}
			return Router.UNREACHABLE_LOCATION;
		}
		
	}

	class SimulLocation extends Simulation {
		public SimulLocation(int id, long endTs) {
			super(id, "Simulating locations " + id, 0, endTs);
			final MyRouter router = new MyRouter(); 
			final ElementType et = new ElementType(this, "Delivery request from home");
			final ResourceType rtTruck = new ResourceType(this, "Delivery truck");
			rtTruck.addGenericResources(NTRUCKS, NOSIZE ? 0 : TRUCKSIZE, router.getHome());
			final WorkGroup wgTruck = new WorkGroup(this, rtTruck, 1);
			final RequestResourcesFlow reqFlow = new RequestResourcesFlow(this, "Request truck");
			reqFlow.newWorkGroupAdder(wgTruck).add();
			final MoveResourcesFlow moveFlow1 = new MoveResourcesFlow(this, "Move truck to home", router.getHome(), router, wgTruck);
			final MoveResourcesFlow moveFlow2 = new MoveResourcesFlow(this, "Move truck to destination", router.getDestination(), router, wgTruck);
			final ReleaseResourcesFlow relFlow = new ReleaseResourcesFlow(this, "Release truck", wgTruck);
			reqFlow.link(moveFlow1).link(moveFlow2).link(relFlow);
			
			new TimeDrivenElementGenerator(this, NELEM, et, reqFlow, new SimulationPeriodicCycle(getTimeUnit(), 0L, new SimulationTimeFunction(getTimeUnit(), "ConstantVariate", getEndTs()), 1));
		}
		
	}

	class LocationListener extends Listener {

		public LocationListener() {
			super("Location listener");
			addEntrance(EntityLocationInfo.class);
			addEntrance(ElementActionInfo.class);
		}

		@Override
		public void infoEmited(SimulationInfo info) {
			System.out.println(info);
		}
		
	}

		@Override
		public Simulation getSimulation(int ind) {
			final SimulLocation sim =  new SimulLocation(ind, ENDTS);
			sim.addInfoReceiver(new LocationListener());
			return sim;
		}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestResourcesLocation().start();;

	}

}
