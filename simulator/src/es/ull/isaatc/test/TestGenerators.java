package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.*;

class SimGenerators extends StandAloneLPSimulation {

	public SimGenerators(double startTs, double endTs, Output out) {
		super("Test de generadores", startTs, endTs, out);
	}

	@Override
	protected void createModel() {
    	Activity actDummy = new Activity(0, this, "Dummy");
        ResourceType crDummy = new ResourceType(0, this, "Dummy");
        WorkGroup wg3 = new WorkGroup(3, this, "");
        wg3.add(crDummy, 1);
        actDummy.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 10.0, 2.0), wg3);
//    	Resource res = new Resource(0, this, "Dummy");
//    	res.addTimeTableEntry(new Cycle(480, RandomVariateFactory.getInstance("ConstantVariate", 1440.0), 0), 480, crDummy);
		SingleMetaFlow metaFlow = new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));     
		PeriodicCycle c = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1440.0), 0);
//		double []val = {0.0, 100.0, 100.0, 50.0, 70.0};
//		Cycle c = new TableCycle(val);
		ElementType etDummy = new ElementType(0, this, "Dummy");
		
		// GENERATORS
		ElementCreator reCre = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));
		reCre.add(etDummy, metaFlow, 1.0);
		new TimeDrivenGenerator(this, reCre, c);
//		FunctionElementCreator funCre = new FunctionElementCreator(new LinearFunction(2.0 / 1440.0, 1.0));
//		funCre.add(etDummy, metaFlow, 1.0);
//		new TimeDrivenGenerator(this, funCre, it);
	}
	
}

/**
 * 
 */
class ExpGenerators extends Experiment {
    static final int NDIAS = 4;
    static final int NPRUEBAS = 1;

	public ExpGenerators(String description) {
		super(description, NPRUEBAS);
	}

	@Override
	public Simulation getSimulation(int ind) {
		SimGenerators sim = new SimGenerators(0.0, NDIAS * 24.0 * 60.0, new Output(true));
		return sim;
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestGenerators {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		new ExpGenerators("Experiment with generators").start();
		double []val = {50.0, 70.0, 100.0, 150.0};
		TableCycle c = new TableCycle(val);
		CycleIterator it = c.iterator(0, 200);
		double v;
		do {
			v = it.next();
			System.out.println(v);
		} while (!Double.isNaN(v));
	}

}
