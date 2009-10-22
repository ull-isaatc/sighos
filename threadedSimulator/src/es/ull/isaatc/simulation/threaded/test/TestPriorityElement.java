/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ParallelFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;
import es.ull.isaatc.simulation.threaded.inforeceiver.StdInfoView;

class PriorityElementSimulation extends StandAloneLPSimulation {
	static final int NACT = 40;
	static final int NELEMT = 4;
	static final int NELEM = 100;
	static final int NRES = 20;
	public PriorityElementSimulation(int id) {
		super(id, "Testing Elements with priority", SimulationTimeUnit.MINUTE, 0.0, 200.0);
		StdInfoView debugView = new StdInfoView(this);
		addInfoReciever(debugView);
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(rt, 2);
		for (int i = 0; i < NACT; i++)
			new TimeDrivenActivity(i, this, "ACT" + i, i / 2).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10), 0, wg);
		SimulationPeriodicCycle c1 = new SimulationPeriodicCycle(this, 0.0, new SimulationTimeFunction(this, "ConstantVariate", 200.0), 0);
		SimulationPeriodicCycle c2 = new SimulationPeriodicCycle(this, 20.0, new SimulationTimeFunction(this, "ConstantVariate", 100.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "RES" + i).addTimeTableEntry(c2, 40, rt);
		ParallelFlow meta = new ParallelFlow(this);
		for (int i = 0; i < NACT; i++)
			new SingleFlow(this, getActivity(i));
		for (int i = 0; i < NELEMT; i++)
			new TimeDrivenGenerator(this, 
					new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(i, this, "ET" + i, i), meta), 
					c1);

	}
	
}
/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestPriorityElement {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PooledExperiment("Testing priority", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				return new PriorityElementSimulation(ind);
			}
			
		}.start();
	}

}
