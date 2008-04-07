/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.util.*;

/**
 * Base model for testing
 * @author Iván Castilla Rodríguez
 */
public class CycleTest {

	public static void testCycle(Cycle c, double start, double end) {
		CycleIterator iter = c.iterator(start, end);
		System.out.println(c); 
		double ts = iter.next();
		while (!Double.isNaN(ts)) {
			System.out.print(ts + " ");
			ts = iter.next();			
		}		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 10-iterations cycle: 0, 1, 2... 10
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 11.0);
		// to-10.0 cycle: 0, 1, 2... 10
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 11.0);
		// 10-iterations cycle: 0, 1, 2... 10. Starting later than 0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 1.0, 11.0);
		// to-10.0 cycle: 0, 1, 2... 10
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 11.0);
		
		Cycle c2 = new PeriodicCycle(24 * 3 + 8, TimeFunctionFactory.getInstance("ConstantVariate", 24), 3);
		Cycle cSemanal = new PeriodicCycle(0, TimeFunctionFactory.getInstance("ConstantVariate", 24 * 7), 7 * 24 + 129.0, c2);
		CycleIterator iter = cSemanal.iterator(24 * 7, 24 * 14);
		System.out.println(cSemanal); 
		double ts = iter.next();
		while (!Double.isNaN(ts)) {
			System.out.print(ts + " ");
			ts = iter.next();			
		}
	}

}
