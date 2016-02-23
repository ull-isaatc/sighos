/**
 * 
 */
package es.ull.iis.test;

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
	
	public static double moreStupidMethod(int iter) {
		double value1 = 21;
		double value2 = 32;

		double result = 0.0;
		for (int i = 1; i < iter; i++)
			result = result + value1 + value2;
		return result;
	}

	public static long moreStupidMethod2(int iter) {
		long value1 = 21;
		long value2 = 32;

		long result = 0;
		for (int i = 1; i < iter; i++)
			result = result + value1 + value2;
		return result;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int nTests = 1000;
		double []tests = new double[nTests]; 
		long t1 = System.nanoTime();
		for (int i = 0; i < nTests; i++) {
			tests[i] = moreStupidMethod(1000000);
		}
		System.out.println("" + ((System.nanoTime() - t1) / (1000 * nTests)) + " microsec");
		System.out.println("" + tests[0]);
	}

}
