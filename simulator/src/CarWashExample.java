/**
 * 
 */
import java.util.ArrayList;

import es.ull.cyc.simulation.*;
import es.ull.cyc.simulation.results.*;
import es.ull.cyc.util.Cycle;
import es.ull.cyc.util.Output;
import es.ull.cyc.random.Exponential;
import es.ull.cyc.random.Fixed;

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
	}

	@Override
	protected ArrayList<Generator> createGenerators() {
		Cycle subC = new Cycle(0.0, new Exponential(11.0), 200.0);
		Cycle c = new Cycle(0.0, new Fixed(1440.0), 0, subC);
		ArrayList<Generator> genList = new ArrayList<Generator>();
		genList.add(new ElementGenerator(this, new Fixed(1), c.iterator(startTs, endTs), new SingleMetaFlow(0, new Fixed(1), getActivity(0))));
		return genList;
	}

	@Override
	protected ArrayList<Resource> createResources() {
		ArrayList<Resource> resList = new ArrayList<Resource>();
		Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
		for (int i = 0; i < NRES; i++) {
			Resource res = new Resource(i, this, "Car washing machine " + i);
			res.addTimeTableEntry(c, 200.0, getResourceType(0));
			resList.add(res);
		}
		return resList;
	}
	
}

class CarWashExperiment extends Experiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;

    CarWashExperiment() {
		super("Car Wash Experiment", NTESTS, new StdResultProcessor(1440.0), new Output(Output.DebugLevel.NODEBUG));
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		return new CarWashSimulation(0.0, 24 * 60.0 * NDAYS, out);
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
