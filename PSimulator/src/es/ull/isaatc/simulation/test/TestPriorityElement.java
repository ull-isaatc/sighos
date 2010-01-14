/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ParallelFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPriorityElement {
	static final int NACT = 40;
	static final int NELEMT = 4;
	static final int NELEM = 100;
	static final int NRES = 20;
	static SimulationFactory.SimulationType simType = SimulationType.GROUPEDX;
	static final TimeUnit unit = TimeUnit.MINUTE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Testing priority", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				SimulationObjectFactory factory = SimulationFactory.getInstance(simType, ind, "Testing Elements with priority", unit, 0, 200);
				Simulation sim = factory.getSimulation();
				
		        ResourceType rt = factory.getResourceTypeInstance(0, "RT0");
		        WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[] {rt}, new int[] {2});
				TimeDrivenActivity acts[] = new TimeDrivenActivity[NACT];
				for (int i = 0; i < NACT; i++) {
					acts[i] = factory.getTimeDrivenActivityInstance(i, "ACT" + i, i / 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class));
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", 10), wg);
				}
				SimulationPeriodicCycle c1 = new SimulationPeriodicCycle(unit, 0, new SimulationTimeFunction(unit, "ConstantVariate", 200), 0);
				SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(unit, 20, new SimulationTimeFunction(unit, "ConstantVariate", 100), 0);
				for (int i = 0; i < NRES; i++)
					factory.getResourceInstance(i, "RES" + i).addTimeTableEntry(c2, 40, rt);

				ParallelFlow meta = (ParallelFlow)factory.getFlowInstance(10, "ParallelFlow");
				for (int i = 0; i < NACT; i++) {
					SingleFlow sin = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
					meta.link(sin);
				}
				for (int i = 0; i < NELEMT; i++)
					factory.getTimeDrivenGeneratorInstance(0, 
							factory.getElementCreatorInstance(0, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), 
									factory.getElementTypeInstance(i, "ET" + i, i), meta), c1);
				
				StdInfoView debugView = new StdInfoView(sim);
				sim.addInfoReceiver(debugView);
				return sim;
			}
			
		}.start();
	}

}
