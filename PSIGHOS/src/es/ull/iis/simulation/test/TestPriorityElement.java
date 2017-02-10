/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPriorityElement {
	static final int NACT = 40;
	static final int NELEMT = 4;
	static final int NELEM = 100;
	static final int NRES = 20;
	static SimulationFactory.SimulationType simType = SimulationType.PARALLEL;
	static final TimeUnit unit = TimeUnit.MINUTE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Testing priority", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Testing Elements with priority", unit, 0, 200);
				Simulation sim = factory.getSimulation();
				
		        ResourceType rt = factory.getResourceTypeInstance("RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {2});
				ActivityFlow<?,?> acts[] = new ActivityFlow[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "ACT" + i, i / 2, EnumSet.noneOf(ActivityFlow.Modifier.class));
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 10), 0, wg);
				}
				SimulationPeriodicCycle c1 = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 200), 0);
				SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(unit, 20, new SimulationTimeFunction(unit, "ConstantVariate", 100), 0);
				for (int i = 0; i < NRES; i++)
					factory.getResourceInstance("RES" + i).addTimeTableEntry(c2, 40, rt);

				ParallelFlow meta = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
				for (int i = 0; i < NACT; i++) {
					meta.link(acts[i]);
				}
				for (int i = 0; i < NELEMT; i++)
					factory.getTimeDrivenGeneratorInstance(
							factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
									factory.getElementTypeInstance("ET" + i, i), meta), c1);
				
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReceiver(debugView);
				return sim;
			}
			
		}.start();
	}

}
