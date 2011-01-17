/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.core.Experiment;
import es.ull.isaatc.simulation.core.ResourceType;
import es.ull.isaatc.simulation.core.Simulation;
import es.ull.isaatc.simulation.core.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.core.SimulationTimeFunction;
import es.ull.isaatc.simulation.core.TimeDrivenActivity;
import es.ull.isaatc.simulation.core.TimeStamp;
import es.ull.isaatc.simulation.core.TimeUnit;
import es.ull.isaatc.simulation.core.WorkGroup;
import es.ull.isaatc.simulation.core.flow.ParallelFlow;
import es.ull.isaatc.simulation.core.flow.SingleFlow;
import es.ull.isaatc.simulation.factory.SimulationFactory;
import es.ull.isaatc.simulation.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.inforeceiver.StdInfoView;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestInterruptibleActivities {
	static final TimeUnit unit = TimeUnit.MINUTE;
	static SimulationFactory.SimulationType simType = SimulationType.PARALLEL;
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
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Testing interruptible activities", unit, TimeStamp.getZero(), new TimeStamp(TimeUnit.MINUTE, 400));
				sim = factory.getSimulation();
				
		        ResourceType rt = factory.getResourceTypeInstance("RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});

				TimeDrivenActivity acts[] = new TimeDrivenActivity[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = factory.getTimeDrivenActivityInstance("ACT" + i, i / 2, EnumSet.of(TimeDrivenActivity.Modifier.INTERRUPTIBLE));
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 101), wg);
				}
				SimulationPeriodicCycle c1 = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 200), 0);
				SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(unit, 20, new SimulationTimeFunction(unit, "ConstantVariate", 100), 0);
				for (int i = 0; i < NRES; i++)
					factory.getResourceInstance("RES" + i).addTimeTableEntry(c2, 40, rt);
				ParallelFlow meta = (ParallelFlow)factory.getFlowInstance("ParallelFlow");
				for (int i = 0; i < NACT; i++) {
					SingleFlow sin = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
					meta.link(sin);
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
