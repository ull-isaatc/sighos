/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.*;

class CycleSim extends Simulation {

	public CycleSim(double startTs, double endTs, Output out) {
		super("TEST", startTs, endTs, out);
	}

	@Override
	protected void createModel() {
		Activity act = new Activity(0, this, "FOO");
		Cycle c1 = new PeriodicCycle(8, new Fixed(24), 0, new PeriodicCycle(1, new Fixed(1), 0));
		ElementCreator ec = new ElementCreator(new Fixed(1));
		ec.add(new ElementType(0, this, "ELEM"), new SingleMetaFlow(0, new Fixed(1), act), 1.0); 
		new TimeDrivenGenerator(this, ec, c1.iterator(startTs, endTs)); 
	}	
}

class CycleExp extends Experiment {
	/**
	 * @param description
	 */
	public CycleExp(String description) {
		super(description, 1);
	}

	@Override
	public Simulation getSimulation(int ind) {
		return new CycleSim(0.0, 48, new Output(true));
	}	
}

/**
 * Base model for testing
 * @author Iv�n Castilla Rodr�guez
 */
public class CycleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CycleExp("Base Experiment").start();
//		Cycle c2 = new PeriodicCycle(24 * 3 + 8, new Fixed(24), 3);
//		Cycle cSemanal = new PeriodicCycle(0, new Fixed(24 * 7), 7 * 24 + 129.0, c2);
//		CycleIterator iter = cSemanal.iterator(24 * 7, 24 * 14);
//		System.out.println(cSemanal); 
//		double ts = iter.next();
//		while (!Double.isNaN(ts)) {
//			System.out.print(ts + " ");
//			ts = iter.next();			
//		}
	}

}