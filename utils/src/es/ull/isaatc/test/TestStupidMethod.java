/**
 * 
 */
package es.ull.isaatc.test;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestStupidMethod {

	public static double stupidMethod() {
		double stupidCalculus = 0.0;
		for (int i = 1; i < 1000; i++)
			stupidCalculus += Math.log(i);
		return stupidCalculus;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nTests = 300;
		double []tests = new double[nTests]; 
		long t1 = System.nanoTime();
		for (int i = 0; i < nTests; i++) {
			tests[i] = stupidMethod();
		}
		System.out.println("" + ((System.nanoTime() - t1) / (1000 * nTests)) + " microsec");
	}

}
