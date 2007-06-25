/**
 * 
 */
package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StdInfoListener;
import es.ull.isaatc.util.PeriodicCycle;

class PriorityElementSimulation extends StandAloneLPSimulation {
	static final int NACT = 40;
	static final int NELEMT = 4;
	static final int NELEM = 100;
	static final int NRES = 20;
	public PriorityElementSimulation(int id) {
		super(id, "Testing Elements with priority", 0.0, 200.0);
		ListenerController cont = new ListenerController();
		setListenerController(cont);
		cont.addListener(new StdInfoListener());
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
 * @author Iván Castilla Rodríguez
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
