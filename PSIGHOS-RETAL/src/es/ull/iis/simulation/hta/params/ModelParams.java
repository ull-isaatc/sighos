/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import java.util.Random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ModelParams {

	/**
	 * 
	 */
	public ModelParams() {
	}

	public static double generateGompertz(double alpha, double beta, double currentAge, double initProb) {
		return Math.log(1-(beta/alpha)*Math.log(1-initProb)*Math.exp(-beta*currentAge))/beta;
	}
	
	@SuppressWarnings("unused")
	private static void testGompertz(int n, double[][] probabilities, double alpha, double beta, Random rng) {
		int []contador1 = new int[probabilities.length+1];
		for (int i = 0; i < n; i++) {
			double time = generateGompertz(alpha, beta, 40, rng.nextDouble()) + 40;
			int interval = 0;
			while (probabilities[interval][1] < time) {
				interval++;
				if (interval >= probabilities.length)
					break;
			}
			if (interval >= probabilities.length)
				contador1[contador1.length-1]++;
			else
				contador1[interval]++;
		}
		for (int j = 0; j < probabilities.length; j++)
			System.out.println("" + probabilities[j][0] + "-" + probabilities[j][1] + ": " + contador1[j]);		
		System.out.println("No event: " + contador1[contador1.length-1]);		
	}

}
