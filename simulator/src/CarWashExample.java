/**
 * 
 */

import es.ull.isaatc.random.Exponential;
import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.ResourceUsageListener;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;

class CarWashSimulation extends Simulation {
	final static int NRES = 2;

	CarWashSimulation(double startTs, double endTs, Output out) {
		super("Car Wash simulation", startTs, endTs, out);
	}
	
	@Override
	protected void createModel() {
		new ResourceType(0, this, "Washing Machine");
		WorkGroup wg = new Activity(0, this, "Wash a car").getNewWorkGroup(0, new Fixed(10));
		wg.add(getResourceType(0), 1);
		new ElementType(0, this, "Car");
		Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
		for (int i = 0; i < NRES; i++)
			new Resource(i, this, "Car washing machine " + i).addTimeTableEntry(c, 200.0, getResourceType(0));
		Cycle subC = new Cycle(0.0, new Exponential(11.0), 200.0);
		Cycle c1 = new Cycle(0.0, new Fixed(1440.0), 0, subC);
		new ElementGenerator(this, new Fixed(1), c1.iterator(startTs, endTs), getElementType(0), new SingleMetaFlow(0, new Fixed(1), getActivity(0)));
	}
}

class CarWashExperiment extends Experiment {
    static final int NDAYS = 2;
    static final int NTESTS = 1;

    CarWashExperiment() {
		super("Car Wash Experiment", NTESTS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		CarWashSimulation sim = new CarWashSimulation(0.0, 24 * 60.0 * NDAYS, new Output(Output.DebugLevel.NODEBUG));
		sim.addListener(new StdInfoListener());
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
