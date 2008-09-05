/**
 * 
 */

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
import es.ull.isaatc.simulation.listener.ElementStartFinishListener;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;

class CarWashSimulation extends StandAloneLPSimulation {
	final static int NRES = 2;

	public CarWashSimulation(int id, SimulationTime startTs, SimulationTime endTs) {
		super(id, "Car Wash simulation", SimulationTimeUnit.MINUTE, startTs, endTs);
	}
	
	@Override
	protected void createModel() {
		new ResourceType(0, this, "Washing Machine");
		WorkGroup wg = new WorkGroup(0, this, ""); 
		wg.add(getResourceType(0), 1);
		new Activity(0, this, "Wash a car").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10), wg);
		new ElementType(0, this, "Car");
//		Cycle c = new PeriodicCycle(0.0, new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", internalEndTs), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "Car washing machine " + i).addTimeTableEntry(c, endTs, getResourceType(0));
//			new Resource(i, this, "Car washing machine " + i).addTimeTableEntry(c, 200.0, getResourceType(0));
//		Cycle subC = new PeriodicCycle(0.0, new SimulationTimeFunction(this, "ExponentialVariate", 11.0), 200.0);
//		Cycle c1 = new PeriodicCycle(0.0, new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0, subC);
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ExponentialVariate", 11.0), 0);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), getElementType(0), new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c1);
	}
}

class CarWashExperiment extends PooledExperiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;

    CarWashExperiment() {
		super("Car Wash Experiment", NTESTS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
//		CarWashSimulation sim = new CarWashSimulation(ind, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), 24 * 60.0 * NDAYS);
		CarWashSimulation sim = new CarWashSimulation(ind, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTime(SimulationTimeUnit.DAY, NDAYS));
		ListenerController cont = new ListenerController() {
			@Override
			public void end() {
				for (String str : getListenerResults()) 
					System.out.println(str);
			}
		};
		sim.setListenerController(cont);
//		cont.addListener(new StdInfoListener());
//		cont.addListener(new StatisticListener(1440.0));
//		cont.addListener(new ResourceUsageListener());
//		cont.addListener(new ElementStartFinishListener(1440.0));
		cont.addListener(new ElementStartFinishListener(200.0));
		cont.addListener(new SimulationTimeListener());
		return sim;
	}
	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CarWashExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CarWashExperiment().start();
	}

}
