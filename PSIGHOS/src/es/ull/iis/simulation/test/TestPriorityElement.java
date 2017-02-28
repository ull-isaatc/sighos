/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ModelPeriodicCycle;
import es.ull.iis.simulation.model.ModelTimeFunction;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.ResourceTypeEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;

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
			public SimulationEngine getSimulation(int ind) {
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Testing Elements with priority", unit, 0, 200);
				SimulationEngine sim = factory.getSimulationEngine();
				
		        ResourceTypeEngine rt = factory.getResourceTypeInstance("RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(new ResourceTypeEngine[] {rt}, new int[] {2});
				ActivityFlow<?,?> acts[] = new ActivityFlow[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "ACT" + i, i / 2, EnumSet.noneOf(ActivityFlow.Modifier.class));
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 10), 0, wg);
				}
				ModelPeriodicCycle c1 = new ModelPeriodicCycle(unit, 0, new ModelTimeFunction(unit, "ConstantVariate", 200), 0);
				ModelPeriodicCycle c2 = new ModelPeriodicCycle(unit, 20, new ModelTimeFunction(unit, "ConstantVariate", 100), 0);
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
