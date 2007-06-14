package es.ull.isaatc.test;
/**
 * 
 */

import java.util.EnumSet;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.listener.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

class TestListenersSimulation extends StandAloneLPSimulation {
	final static int NRES = 9;

	TestListenersSimulation() {
		super("TestListeners simulation");
	}
	
	@Override
	protected void createModel() {
		new ResourceType(1, this, "RT 1");
		new ResourceType(2, this, "RT 2");
		
		WorkGroup wg1= new WorkGroup(1, this, "",
				new ResourceType[] {getResourceType(1)},
				new int[] {1});
		WorkGroup wg2= new WorkGroup(1, this, "",
				new ResourceType[] {getResourceType(2)},
				new int[] {1});
 
		EnumSet<Activity.Modifier> modifiers = EnumSet.noneOf(Activity.Modifier.class);
		modifiers.add(Activity.Modifier.INTERRUPTIBLE);
		
		new Activity(1, this, "ACT 1", modifiers).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 15), wg1);
		new Activity(2, this, "ACT 2", modifiers).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10), wg2);

		new ElementType(1, this, "ET 1");
		new ElementType(2, this, "ET 2");
		
		Cycle c = new PeriodicCycle(480.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0);
		for (int i = 0; i < NRES; i++) {
			new Resource(i, this, "REST1 " + i).addTimeTableEntry(c, 480.0, getResourceType(1));
			new Resource(NRES + i, this, "REST2 " + i).addTimeTableEntry(c, 480.0, getResourceType(2));
		}
		Resource r = new Resource(2 * NRES, this, "RES " + 2 * NRES);
		r.addTimeTableEntry(c, 480.0, getResourceType(1));
		r.addTimeTableEntry(c, 480.0, getResourceType(2));
		
		Cycle subC = new PeriodicCycle(480.0, TimeFunctionFactory.getInstance("ExponentialVariate", 10.0), 960.0);
		Cycle c1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0, subC);
		
		new TimeDrivenGenerator(this,
				new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 4),
						getElementType(1),
						new SingleMetaFlow(1, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1))),
				c1);
		new TimeDrivenGenerator(this,
				new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 6),
						getElementType(2),
						new SingleMetaFlow(2, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2))),
				c1);
	}
}

class TestListenersExperiment extends Experiment {
    static final int NDAYS = 5;
    static final int NTESTS = 1;

    TestListenersExperiment() {
		super("TestListener Experiment", NTESTS, 0.0, 24 * 60.0 * NDAYS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		TestListenersSimulation sim = new TestListenersSimulation();
//		sim.addListener(new StdInfoListener(System.out));
		sim.addListener(new ActivityListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		sim.addListener(new ActivityTimeListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		sim.addListener(new ElementStartFinishListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		sim.addListener(new ElementTypeTimeListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});		
		sim.addListener(new ResourceStdUsageListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});			return sim;
	}
	
}

/**
 * @author Roberto Muñoz
 */
public class TestListeners {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestListenersExperiment().start();
	}

}
