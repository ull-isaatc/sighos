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
		System.out.println(c + "\t(FROM " + start + " TO " + end + ")"); 
		double ts = iter.next();
		while (!Double.isNaN(ts)) {
			System.out.print(ts + "\t");
			ts = iter.next();			
		}
		System.out.println();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 0.0, 200.0);
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 1.0, 200.0);
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 0.0, 4.0);
		// 10-iterations cycle: 0, 1, 2... 9
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 200.0);
		// to-10.0 cycle: 0, 1, 2... 9
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 0.0, 200.0);
		// 10-iterations cycle: 1, 2... 9. Starting later than 0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 1.0, 200.0);
		// to-10.0 cycle: 0, 1, 2... 9. Starting later than 0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 1.0, 200.0);
		// 10-iterations cycle: 0, 2... 9. Ending before 9
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 8.9);
		// to-10.0 cycle: 0, 1, 2... 9. Ending before 9
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 0.0, 8.9);
		
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0
		Cycle subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 2);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 200.0);
		// 2 iterations each 10: 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. Starting at 1
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 1.0, 200.0);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 22.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 22.0);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 20.5
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 20.5);

		// 3 iterations each 2.1: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0
		subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 3);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 2 iterations each 10: 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. Starting at 1
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 22.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 20.5
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);

		// 3 iterations each 1.1 with longer period: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0
		subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), 3);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 2 iterations each 10: 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. Starting at 1
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 22.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 2 iterations each 10: 0.0, 1.0, 10.0, 11.0, 20.0, 21.0, 30.0, 31.0. ending at 20.5
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);

//		Cycle c2 = new PeriodicCycle(24 * 3 + 8, TimeFunctionFactory.getInstance("ConstantVariate", 24), 3);
//		Cycle cSemanal = new PeriodicCycle(0, TimeFunctionFactory.getInstance("ConstantVariate", 24 * 7), 7 * 24 + 129.0, c2);
//		CycleIterator iter = cSemanal.iterator(24 * 7, 24 * 14);
	}

}

