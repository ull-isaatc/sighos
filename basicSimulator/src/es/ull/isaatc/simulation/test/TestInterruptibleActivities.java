/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.EnumSet;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SimultaneousMetaFlow;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StdInfoListener;

class InterruptibleActivitiesSimulation extends StandAloneLPSimulation {
	static final int NACT = 1;
	static final int NELEMT = 1;
	static final int NELEM = 2;
	static final int NRES = 1;
	public InterruptibleActivitiesSimulation(int id) {
		super(id, "Testing interruptible activities", SimulationTimeUnit.MINUTE, new SimulationTime(SimulationTimeUnit.MINUTE,0.0), new SimulationTime(SimulationTimeUnit.MINUTE,400.0));
		ListenerController cont = new ListenerController();
		setListenerController(cont);
		cont.addListener(new StdInfoListener());
	}

	@Override
	protected void createModel() {
		ResourceType rt = new ResourceType(0, this, "RT0");
		WorkGroup wg = new WorkGroup(0, this, "WG");
		wg.add(rt, 1);
		for (int i = 0; i < NACT; i++)
			new Activity(i, this, "ACT" + i, i / 2, EnumSet.of(Activity.Modifier.INTERRUPTIBLE)).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 101), 0, wg);
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE,0.0), new SimulationTimeFunction(this, "ConstantVariate", 200.0), 0);
		SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE,20.0), new SimulationTimeFunction(this, "ConstantVariate", 100.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "RES" + i).addTimeTableEntry(c2, new SimulationTime(SimulationTimeUnit.MINUTE, 40), rt);
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
		new PooledExperiment("Testing interruptible activities", 1) {

			@Override
			public Simulation getSimulation(int ind) {
				return new InterruptibleActivitiesSimulation(ind);
			}
			
		}.start();
	}

}
