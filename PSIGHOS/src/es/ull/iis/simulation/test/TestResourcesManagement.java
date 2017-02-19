/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.sequential.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class TestResourcesManagement extends Experiment {
	final static int N_EXP = 1;
	final static TimeUnit UNIT = TimeUnit.MINUTE;
	final static long END_TIME = 100;

	final private Model model;
	
	class ModelResourceManagement extends Model {
		public ModelResourceManagement() {
			super();
			
			// The only element type
			final ElementType et = new ElementType(this, "Package");
			
			// The three resource types involved in the simulation
			final ResourceType rtOperatorA = new ResourceType(this, "OperatorA");
			final ResourceType rtOperatorB = new ResourceType(this, "OperatorB");
			final ResourceType rtMachine = new ResourceType(this, "Machine");
			final ResourceType rtTransport = new ResourceType(this, "Transport");
			final ResourceType rtLocationA = new ResourceType(this, "LocationA");
			final ResourceType rtLocationB = new ResourceType(this, "LocationB");

			// Create the specific resources
			rtOperatorA.addGenericResources(1);
			rtOperatorB.addGenericResources(1);
			rtMachine.addGenericResources(1);
			rtTransport.addGenericResources(1);
			rtLocationA.addGenericResources(2);
			rtLocationB.addGenericResources(1);
			
			// Define the workgroups
			final WorkGroup wgLocationA = new WorkGroup(this, rtLocationA, 1);
			final WorkGroup wgLocationB = new WorkGroup(this, rtLocationB, 1);
			final WorkGroup wgOperatorA = new WorkGroup(this, rtOperatorA, 1);
			final WorkGroup wgOperatorB = new WorkGroup(this, rtOperatorB, 1);
			final WorkGroup wgTransport = new WorkGroup(this, rtTransport, 1);
			final WorkGroup wgMachine = new WorkGroup(this, rtMachine, 1);
			final WorkGroup wgEmpty = new WorkGroup(this);
			
			// Create basic steps of the flow
			final RequestResourcesFlow reqLocationA = new RequestResourcesFlow(this, "Request location A", 0);
			final RequestResourcesFlow reqLocationB = new RequestResourcesFlow(this, "Request location B", 1);
			final RequestResourcesFlow reqOperatorA = new RequestResourcesFlow(this, "Request operator A", 2);
			final RequestResourcesFlow reqTransport = new RequestResourcesFlow(this, "Request transport", 3);
			final ReleaseResourcesFlow relLocationA = new ReleaseResourcesFlow(this, "Request location A", 0);
			final ReleaseResourcesFlow relLocationB = new ReleaseResourcesFlow(this, "Release location B", 1);
			final ReleaseResourcesFlow relOperatorA = new ReleaseResourcesFlow(this, "Release operator A", 2);
			final ReleaseResourcesFlow relTransport = new ReleaseResourcesFlow(this, "Release transport", 3);
			
//			final ActivityFlow actWorkAtLocationA = new ActivityFlow(this, "Work at location A");
//			final ActivityFlow actWorkAtLocationB = new ActivityFlow(this, "Work at location B");
//			final ActivityFlow actMoveFromAToB = new ActivityFlow(this, "Move from A to B");
			
			// Assign duration and workgroups to activities
			reqLocationA.addWorkGroup(wgLocationA);
			reqLocationB.addWorkGroup(wgLocationB);
			reqOperatorA.addWorkGroup(wgOperatorA);
			reqTransport.addWorkGroup(wgTransport);
//			actWorkAtLocationA.addWorkGroup(10, 0, wgMachine);
//			actWorkAtLocationB.addWorkGroup(10, 0, wgOperatorB);
//			actMoveFromAToB.addWorkGroup(5, 0, wgEmpty);

			// Create flow
			reqLocationA.link(reqOperatorA)./*link(actWorkAtLocationA).*/link(relOperatorA).link(reqTransport).link(relLocationA);
			relLocationA./*link(actMoveFromAToB).*/link(reqLocationB).link(relTransport)./*link(actWorkAtLocationB).*/link(relLocationB);
			SimulationPeriodicCycle cycle = SimulationPeriodicCycle.newDailyCycle(UNIT, 0);
			new TimeDrivenGenerator(this, 1, et, reqLocationA, cycle);
		}
	}
	
	/**
	 * 
	 */
	public TestResourcesManagement(int nExperiments) {
		super("Testing resource management", nExperiments);
		this.model = new ModelResourceManagement();
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new Simulation(ind, "Testing resource management " + ind, model, 0, END_TIME);
		sim.addInfoReceiver(new StdInfoView(sim));
		return sim;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestResourcesManagement(N_EXP).start();

	}

}
