/**
 * 
 */
package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.*;

class CycleSim extends StandAloneLPSimulation {

	public CycleSim() {
		super("TEST");
	}

	@Override
	protected void createModel() {
		Activity act = new Activity(0, this, "FOO");
		Cycle c1 = new PeriodicCycle(8, TimeFunctionFactory.getInstance("ConstantVariate", 24), 0, new PeriodicCycle(1, TimeFunctionFactory.getInstance("ConstantVariate", 1), 0));
		ElementCreator ec = new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1));
		ec.add(new ElementType(0, this, "ELEM"), new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1), act), 1.0); 
		new TimeDrivenGenerator(this, ec, c1); 
	}	
}

class CycleExp extends Experiment {
	/**
	 * @param description
	 */
	public CycleExp(String description) {
		super(description, 1, 0.0, 48);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new CycleSim();
		sim.setOutput(new Output(true));
		return sim;
	}	
}

/**
 * Base model for testing
 * @author Iván Castilla Rodríguez
 */
public class CycleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CycleExp("Base Experiment").start();
//		Cycle c2 = new PeriodicCycle(24 * 3 + 8, RandomVariateFactory.getInstance("ConstantVariate", 24), 3);
//		Cycle cSemanal = new PeriodicCycle(0, RandomVariateFactory.getInstance("ConstantVariate", 24 * 7), 7 * 24 + 129.0, c2);
//		CycleIterator iter = cSemanal.iterator(24 * 7, 24 * 14);
//		System.out.println(cSemanal); 
//		double ts = iter.next();
//		while (!Double.isNaN(ts)) {
//			System.out.print(ts + " ");
//			ts = iter.next();			
//		}
	}

}
