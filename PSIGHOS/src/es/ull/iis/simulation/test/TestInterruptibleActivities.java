/**
 * 
 */
package es.ull.iis.simulation.test;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.Experiment;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.factory.SimulationFactory;
import es.ull.iis.simulation.core.factory.SimulationObjectFactory;
import es.ull.iis.simulation.core.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.ParallelFlow;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ModelPeriodicCycle;
import es.ull.iis.simulation.model.ModelTimeFunction;
import es.ull.iis.simulation.model.ResourceTypeEngine;
import es.ull.iis.simulation.model.SimulationEngine;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestInterruptibleActivities {
	static final TimeUnit unit = TimeUnit.MINUTE;
	static SimulationFactory.SimulationType simType = SimulationType.SEQUENTIAL;
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Testing interruptible activities", 1) {

			@Override
			public SimulationEngine<?> getSimulation(int ind) {
				SimulationEngine<?> sim = null;
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Testing interruptible activities", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.MINUTE, 400));
				sim = factory.getSimulation();
				
		        ResourceTypeEngine rt = factory.getResourceTypeInstance("RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(new ResourceTypeEngine[] {rt}, new int[] {1});

				ActivityFlow<?,?> acts[] = new ActivityFlow[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = (ActivityFlow<?,?>)factory.getFlowInstance("ActivityFlow", "ACT" + i, i / 2, EnumSet.of(ActivityFlow.Modifier.INTERRUPTIBLE));
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", 101), 0, wg);
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
				
				sim.addInfoReceiver(new StdInfoView(sim));
				return sim;
			}
			
		}.start();
	}

}
