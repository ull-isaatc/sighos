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

class InterruptibleActivitiesSimulation extends StandAloneLPSimulation {
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;
	public InterruptibleActivitiesSimulation() {
		super("Testing interruptible activities");
//		addListener(new StdInfoListener());
		setOutput(new Output(true));
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(0, this, "WG");
		wg.add(rt, 1);
		for (int i = 0; i < NACT; i++)
			new Activity(i, this, "ACT" + i, i / 2, true, true).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 101), 0, wg);
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
 * @author Iván Castilla Rodríguez
 *
 */
public class TestInterruptibleActivities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Testing interruptible activities", 1, 0.0, 400.0) {

			@Override
			public Simulation getSimulation(int ind) {
				return new InterruptibleActivitiesSimulation();
			}
			
		}.start();
	}

}
