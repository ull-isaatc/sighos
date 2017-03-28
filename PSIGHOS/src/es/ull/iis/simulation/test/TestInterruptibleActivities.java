/**
 * 
 */
package es.ull.iis.simulation.test;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.ParallelFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestInterruptibleActivities {
	static final TimeUnit unit = TimeUnit.MINUTE;
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
			public Simulation getSimulation(int ind) {
				Simulation sim = null;
				SimulationFactory factory = new SimulationFactory(ind, "Testing interruptible activities", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.MINUTE, 400));
				sim = factory.getSimulation();
				
		        ResourceType rt = factory.getResourceTypeInstance("RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});

				ActivityFlow acts[] = new ActivityFlow[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "ACT" + i, i / 2, false, true);
					acts[i].addWorkGroup(0, wg, new SimulationTimeFunction(unit, "ConstantVariate", 101));
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
					factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
									factory.getElementTypeInstance("ET" + i, i), meta, c1);
				
				sim.addInfoReceiver(new StdInfoView());
				return sim;
			}
			
		}.start();
	}

}
