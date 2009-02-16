package es.ull.isaatc.simulation.test;
/**
 * 
 */

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
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.listener.ActivityListener;
import es.ull.isaatc.simulation.listener.ActivityTimeListener;
import es.ull.isaatc.simulation.listener.ElementStartFinishListener;
import es.ull.isaatc.simulation.listener.ElementTypeTimeListener;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.ResourceStdUsageListener;

class TestListenersSimulation extends StandAloneLPSimulation {
	final static int NRES = 9;

	TestListenersSimulation(int id, SimulationTime startTs, SimulationTime endTs) {
		super(id, "TestListeners simulation", SimulationTimeUnit.MINUTE, startTs, endTs);
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
		
		new Activity(1, this, "ACT 1", modifiers).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 15), wg1);
		new Activity(2, this, "ACT 2", modifiers).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10), wg2);

		new ElementType(1, this, "ET 1");
		new ElementType(2, this, "ET 2");
		
		SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		for (int i = 0; i < NRES; i++) {
			new Resource(i, this, "REST1 " + i).addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(1));
			new Resource(NRES + i, this, "REST2 " + i).addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(2));
		}
		Resource r = new Resource(2 * NRES, this, "RES " + 2 * NRES);
		r.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(1));
		r.addTimeTableEntry(c, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), getResourceType(2));
		
		SimulationCycle subC = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480.0), new SimulationTimeFunction(this, "ExponentialVariate", 10.0), new SimulationTime(SimulationTimeUnit.MINUTE, 960.0));
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0, subC);
		
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

class TestListenersExperiment extends PooledExperiment {
    static final int NDAYS = 5;
    static final int NTESTS = 1;

    TestListenersExperiment() {
		super("TestListener Experiment", NTESTS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		TestListenersSimulation sim = new TestListenersSimulation(ind, SimulationTime.getZero(), new SimulationTime(SimulationTimeUnit.DAY, NDAYS));
		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
//		sim.addListener(new StdInfoListener(System.out));
		cont.addListener(new ActivityListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		cont.addListener(new ActivityTimeListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		cont.addListener(new ElementStartFinishListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});
		cont.addListener(new ElementTypeTimeListener(1440) {
			@Override
			public void infoEmited(SimulationEndInfo info) {
				super.infoEmited(info);
				System.out.print(this);
			}
		});		
		cont.addListener(new ResourceStdUsageListener(1440) {
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
