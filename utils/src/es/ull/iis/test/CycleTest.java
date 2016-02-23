/**
 * 
 */
package es.ull.iis.test;

import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.util.Cycle;
import es.ull.iis.util.CycleIterator;
import es.ull.iis.util.PeriodicCycle;
import es.ull.iis.util.RoundedPeriodicCycle;
import es.ull.iis.util.TableCycle;
import es.ull.iis.util.WeeklyPeriodicCycle;

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

	public static void tableCycleTest() {
		// 0.0	1.0	2.0	3.0	4.0	5.0
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 0.0, 200.0);
		// 1.0	2.0	3.0	4.0	5.0
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 1.0, 200.0);
		// 0.0	1.0	2.0	3.0
		testCycle(new TableCycle(new double[] {0.0, 1.0, 2.0, 3.0, 4.0, 5.0}), 0.0, 4.0);
	}
	
	public static void periodicCycleTest() {
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 200.0);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 0.0, 200.0);
		// 1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 1.0, 200.0);
		// 1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 1.0, 200.0);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10), 0.0, 8.9);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0), 0.0, 8.9);
	}
	
	public static void periodicSubCycleTest() {
		// 0.0	1.0	10.0	11.0	20.0	21.0	30.0	31.0
		Cycle subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 2);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 200.0);
		// 1.0	10.0	11.0	20.0	21.0	30.0	31.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 1.0, 200.0);
		// 0.0	1.0	10.0	11.0	20.0	21.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 22.0);
		// 0.0	1.0	10.0	11.0	20.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 20.5);

		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999	9.899999999999999
		subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 3.0);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999	9.899999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);

		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999	9.899999999999999
		subC1 = new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), 3);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999	9.899999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);		
	}
	
	public static void roundedPeriodicCycleTest(RoundedPeriodicCycle.Type type, double factor) {
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10, type, factor), 0.0, 200.0);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0, type, factor), 0.0, 200.0);
		// 1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10, type, factor), 1.0, 200.0);
		// 1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0	9.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0, type, factor), 1.0, 200.0);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10, type, factor), 0.0, 8.9);
		// 0.0	1.0	2.0	3.0	4.0	5.0	6.0	7.0	8.0
		testCycle(new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 10.0, type, factor), 0.0, 8.9);
	}
	
	public static void roundedPeriodicSubCycleTest(RoundedPeriodicCycle.Type type, double factor) {
		// 0.0	1.0	10.0	11.0	20.0	21.0	30.0	31.0
		Cycle subC1 = new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 2, type, factor);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 200.0);
		// 1.0	10.0	11.0	20.0	21.0	30.0	31.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 1.0, 200.0);
		// 0.0	1.0	10.0	11.0	20.0	21.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 22.0);
		// 0.0	1.0	10.0	11.0	20.0
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 20.5);

		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999	9.899999999999999
		subC1 = new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 3.0, type, factor);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999	9.899999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 0.0	1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);

		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999	9.899999999999999
		subC1 = new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 3.0), 3, type, factor);
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999	9.899999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 9.8);
		// 0.0	1.1	2.2	3.3000000000000003	4.4	5.5	6.6	7.699999999999999	8.799999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 8.9);		
	}
	
	public static void weeklyPeriodicCycleTest() {
		testCycle(new WeeklyPeriodicCycle(WeeklyPeriodicCycle.WEEKDAYS, 1.0, 0.0, 0), 0.0, 21.0);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Testing constant cycles");
//		tableCycleTest();
//
//		periodicCycleTest();
//		periodicSubCycleTest();
		
//		roundedPeriodicCycleTest(RoundedPeriodicCycle.Type.ROUND, 5);
//		roundedPeriodicSubCycleTest(RoundedPeriodicCycle.Type.ROUND, 5);
//		roundedPeriodicCycleTest(RoundedPeriodicCycle.Type.FLOOR, 5);
//		roundedPeriodicCycleTest(RoundedPeriodicCycle.Type.CEIL, 5);
//		Cycle subC1 = new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 3.5), 3, RoundedPeriodicCycle.Type.ROUND, 5);
//		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 10.0), 40.0, subC1), 0.0, 200.0);
		// FIXME: Este ciclo peta
		Cycle subC1 = new RoundedPeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.0), 3.0, RoundedPeriodicCycle.Type.ROUND, 5);
//		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 0.0, 200.0);
		// 1.0	1.1	2.1	2.2	3.2	3.3000000000000003	4.300000000000001	4.4	5.4	5.5	6.5	6.6	7.6	7.699999999999999	8.7	8.799999999999999	9.799999999999999	9.899999999999999
		testCycle(new PeriodicCycle(0.0, TimeFunctionFactory.getInstance("ConstantVariate", 1.1), 10.0, subC1), 1.0, 200.0);
//		weeklyPeriodicCycleTest();
	}

}

