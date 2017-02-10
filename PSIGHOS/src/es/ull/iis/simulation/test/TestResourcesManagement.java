/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.sequential.ElementCreator;
import es.ull.iis.simulation.sequential.ElementType;
import es.ull.iis.simulation.sequential.ResourceType;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.TimeDrivenGenerator;
import es.ull.iis.simulation.sequential.WorkGroup;
import es.ull.iis.simulation.sequential.flow.ActivityFlow;
import es.ull.iis.simulation.sequential.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.sequential.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class TestResourcesManagement extends Experiment {
	final static int N_EXP = 1;
	final static TimeUnit UNIT = TimeUnit.MINUTE;
	final static long END_TIME = 100;

	class SimulationResourcesManagement extends Simulation {
		
		public SimulationResourcesManagement(int id) {
			super(id, "Testing resource management " + id, UNIT, 0, END_TIME);

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
			final WorkGroup wgLocationA = new WorkGroup(rtLocationA, 1);
			final WorkGroup wgLocationB = new WorkGroup(rtLocationB, 1);
			final WorkGroup wgOperatorA = new WorkGroup(rtOperatorA, 1);
			final WorkGroup wgOperatorB = new WorkGroup(rtOperatorB, 1);
			final WorkGroup wgTransport = new WorkGroup(rtTransport, 1);
			final WorkGroup wgMachine = new WorkGroup(rtMachine, 1);
			final WorkGroup wgEmpty = new WorkGroup();
			
			// Create basic steps of the flow
			final RequestResourcesFlow reqLocationA = new RequestResourcesFlow(this, "Request location A", 0);
			final RequestResourcesFlow reqLocationB = new RequestResourcesFlow(this, "Request location B", 1);
			final RequestResourcesFlow reqOperatorA = new RequestResourcesFlow(this, "Request operator A", 2);
			final RequestResourcesFlow reqTransport = new RequestResourcesFlow(this, "Request transport", 3);
			final ReleaseResourcesFlow relLocationA = new ReleaseResourcesFlow(this, "Request location A", 0);
			final ReleaseResourcesFlow relLocationB = new ReleaseResourcesFlow(this, "Release location B", 1);
			final ReleaseResourcesFlow relOperatorA = new ReleaseResourcesFlow(this, "Release operator A", 2);
			final ReleaseResourcesFlow relTransport = new ReleaseResourcesFlow(this, "Release transport", 3);
			
			final ActivityFlow actWorkAtLocationA = new ActivityFlow(this, "Work at location A");
			final ActivityFlow actWorkAtLocationB = new ActivityFlow(this, "Work at location B");
			final ActivityFlow actMoveFromAToB = new ActivityFlow(this, "Move from A to B");
			
			// Assign duration and workgroups to activities
			reqLocationA.addWorkGroup(wgLocationA);
			reqLocationB.addWorkGroup(wgLocationB);
			reqOperatorA.addWorkGroup(wgOperatorA);
			reqTransport.addWorkGroup(wgTransport);
			actWorkAtLocationA.addWorkGroup(10, 0, wgMachine);
			actWorkAtLocationB.addWorkGroup(10, 0, wgOperatorB);
			actMoveFromAToB.addWorkGroup(5, 0, wgEmpty);

			// Create flow
			reqLocationA.link(reqOperatorA).link(actWorkAtLocationA).link(relOperatorA).link(reqTransport).link(relLocationA);
			relLocationA.link(actMoveFromAToB).link(reqLocationB).link(relTransport).link(actWorkAtLocationB).link(relLocationB);
			SimulationPeriodicCycle cycle = SimulationPeriodicCycle.newDailyCycle(unit, 0);
			ElementCreator creator = new ElementCreator(this, 2, et, reqLocationA);
			new TimeDrivenGenerator(this, creator, cycle);
		}
	}
	
	/**
	 * 
	 */
	public TestResourcesManagement(int nExperiments) {
		super("Testing resource management", nExperiments);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new SimulationResourcesManagement(ind);
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
