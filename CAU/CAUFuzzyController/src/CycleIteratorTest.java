import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;
import es.ull.isaatc.util.PeriodicCycle;


public class CycleIteratorTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Cycle cycle = new PeriodicCycle(780,TimeFunctionFactory.getInstance("ConstantVariate", 1440), 0);
		CycleIterator cIter = cycle.iterator(0, 86400);
		double ts = cIter.next();
		while (!Double.isNaN(ts)) {
			System.out.println(ts);
			ts = cIter.next();
		}
	}

}
