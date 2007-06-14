/**
 * 
 */

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.listener.ResourceUsageListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

class CarWashSimulation extends StandAloneLPSimulation {
	final static int NRES = 2;

	CarWashSimulation() {
		super("Car Wash simulation");
	}
	
	@Override
	protected void createModel() {
		new ResourceType(0, this, "Washing Machine");
		WorkGroup wg = new WorkGroup(0, this, ""); 
		wg.add(getResourceType(0), 1);
		new Activity(0, this, "Wash a car").addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10), wg);
		new ElementType(0, this, "Car");
		Cycle c = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "Car washing machine " + i).addTimeTableEntry(c, 200.0, getResourceType(0));
		Cycle subC = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ExponentialVariate", 11.0), 200.0);
		Cycle c1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0, subC);
		new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), getElementType(0), new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0))), c1);
	}
}

class CarWashExperiment extends Experiment {
    static final int NDAYS = 2;
    static final int NTESTS = 1;

    CarWashExperiment() {
		super("Car Wash Experiment", NTESTS, 0.0, 24 * 60.0 * NDAYS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		CarWashSimulation sim = new CarWashSimulation();
		sim.addListener(new StdInfoListener(System.out));
//		sim.addListener(new StatisticListener(1440.0));
		sim.addListener(new ResourceUsageListener());
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
