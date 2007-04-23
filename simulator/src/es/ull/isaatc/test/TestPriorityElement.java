/**
 * 
 */
package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

class PriorityElementSimulation extends StandAloneLPSimulation {
	static final int NACT = 2;
	static final int NELEMT = 3;
	static final int NELEM = 3;
	static final int NRES = 18;
	public PriorityElementSimulation() {
		super("Testing Elements with priority");
		addListener(new StdInfoListener());
//		setOutput(new Output(true));
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(0, this, "WG");
		wg.add(rt, 2);
		for (int i = 0; i < NACT; i++)
			new Activity(i, this, "ACT" + i, i / 2).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10), 0, wg);
		PeriodicCycle c1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 200.0), 0);
		PeriodicCycle c2 = new PeriodicCycle(20.0, TimeFunctionFactory.getInstance("ConstantVariate", 100.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "RES" + i).addTimeTableEntry(c2, 40, rt);
		SimultaneousMetaFlow meta = new SimultaneousMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1));
		for (int i = 0; i < NACT; i++)
			new SingleMetaFlow(i, meta, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(i));
		for (int i = 0; i < NELEMT; i++)
			new TimeDrivenGenerator(this, 
					new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM), new ElementType(i, this, "ET" + i, i), meta), 
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
		new Experiment("Testing priority", 1, 0.0, 200.0) {

			@Override
			public Simulation getSimulation(int ind) {
				return new PriorityElementSimulation();
			}
			
		}.start();
	}

}