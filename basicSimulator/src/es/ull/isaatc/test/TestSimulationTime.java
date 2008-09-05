/**
 * 
 */
package es.ull.isaatc.test;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestSimulationTime {
	final static long NITER = 1000000000;
	public static void main(String[] args) {
		System.out.println(Double.SIZE);
		long t1, t2;
		double val1 = 1212412412341.1;
		double val2 = 264363243432.14;
		t1 = System.currentTimeMillis();
		for (long i = 0; i < NITER; i++)
			val1 = val1 + val2;
		t2 = System.currentTimeMillis() - t1;
		System.out.println("T. DOUBLE:\t" + t2);

		long v1 = 1;
		long v2 = 2;
		t1 = System.currentTimeMillis();
		for (long i = 0; i < NITER; i++)
			v1 = v1 + v2;
		t2 = System.currentTimeMillis() - t1;
		System.out.println("T. LONG:\t" + t2);
		
		
//		SimulationTime valt1 = new SimulationTime(SimulationTime.Unit.MINUTE, 1);
//		SimulationTime valt2 = new SimulationTime(SimulationTime.Unit.MINUTE, 2);
//		SimulationTime valt3;
//		t1 = System.currentTimeMillis();
//		for (long i = 0; i < NITER; i++)
//			valt3 = new SimulationTime(SimulationTime.Unit.MINUTE, valt1.getValue() + valt2.getValue());
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println("T. NEW CLASS:\t" + t2);
//		
//		valt3 = new SimulationTime(SimulationTime.Unit.MINUTE, 0);
//		t1 = System.currentTimeMillis();
//		for (long i = 0; i < NITER; i++)
//			valt3.setValue(valt1.getValue() + valt2.getValue());
//		t2 = System.currentTimeMillis() - t1;
//		System.out.println("T. SET:\t" + t2);
		
	}
}
