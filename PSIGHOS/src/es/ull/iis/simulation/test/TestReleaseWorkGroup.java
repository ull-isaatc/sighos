/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.TimeDrivenElementGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;

/**
 * @author Iván Castilla
 *
 */
public class TestReleaseWorkGroup extends Experiment {
	final static int N_EXP = 1;
	final static TimeUnit UNIT = TimeUnit.MINUTE;
	final static long END_TIME = 100;

	class ModelReleaseManagement extends Simulation {
		public ModelReleaseManagement (int ind) {
			super(ind, "Testing resource management " + ind, UNIT, 0, END_TIME);
			
			// The only element type
			final ElementType et = new ElementType(this, "Package");
			
			// The three resource types involved in the simulation
			final ResourceType rtOperatorA = new ResourceType(this, "OperatorA");
			final ResourceType rtMachine = new ResourceType(this, "Machine");
			final ResourceType rtLocationA = new ResourceType(this, "LocationA");

			// Create the specific resources
			rtOperatorA.addGenericResources(2);
			rtMachine.addGenericResources(2);
			rtLocationA.addGenericResources(1);
			
			// Define the workgroups
			final WorkGroup wgLocationA = new WorkGroup(this, new ResourceType[] {rtLocationA, rtOperatorA, rtMachine}, new int[] {1,2,2});
			final WorkGroup wgRelLocationA1 = new WorkGroup(this, new ResourceType[] {rtLocationA, rtMachine}, new int[] {1,1});
			final WorkGroup wgRelLocationA2 = new WorkGroup(this, new ResourceType[] {rtMachine, rtOperatorA}, new int[] {1,2});

			// Create basic steps of the flow
			final RequestResourcesFlow reqLocationA = new RequestResourcesFlow(this, "Request location A", 0);
			final ReleaseResourcesFlow relLocationA1 = new ReleaseResourcesFlow(this, "Release location A 1", 0, wgRelLocationA1);
			final ReleaseResourcesFlow relLocationA2 = new ReleaseResourcesFlow(this, "Release location A 2", 0, wgRelLocationA2);
			
			// Assign duration and workgroups to activities
			reqLocationA.addWorkGroup(wgLocationA);

			// Create flow
			reqLocationA.link(relLocationA1).link(relLocationA2);
			SimulationPeriodicCycle cycle = SimulationPeriodicCycle.newDailyCycle(UNIT, 0);
			new TimeDrivenElementGenerator(this, 1, et, reqLocationA, cycle);
		}
	}
	
	/**
	 * 
	 */
	public TestReleaseWorkGroup(int nExperiments) {
		super("Testing resource management", nExperiments);
	}

	@Override
	public Simulation getSimulation(int ind) {
		final Simulation model = new ModelReleaseManagement(ind);
		model.addInfoReceiver(new StdInfoView());
		return model;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestReleaseWorkGroup(N_EXP).start();

	}

}
