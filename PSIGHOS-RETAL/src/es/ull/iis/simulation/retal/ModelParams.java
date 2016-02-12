/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.Random;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ModelParams {

	/**
	 * 
	 * @param baseCase True if base case parameters (expected values) should be used. False for second order simulations.
	 */
	public ModelParams(boolean baseCase) {
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

	/**
	 * Returns time to event according to an empirical distribution. The distribution is defined 
	 * as a set of trios <low limit of time interval, high limit of time interval, probability>. 
	 * An initial probability is used to compare with every interval: if such probability is lower
	 * than the probability defined for the interval, the event is supposed to happen at this interval.
	 * The event cannot happen in the past. The current age of the individual is used to ensure so.
	 * @param probabilities An array of trios <low limit of time interval, high limit of time interval, 
	 * probability>
	 * @param currentAge Current age of the individual
	 * @param initProbs Initial probability of the event to happen to this individual at each time interval
	 * @return The time when the event is supposed to happen. Double.MAX_VALUE in case
	 * the event is not happening
	 */
	public static double getTimeToEvent(double[][] probabilities, double currentAge, double []initProb) {
		// Start by assigning "infinite" to ageAtEvent
		double ageAtEvent = Double.MAX_VALUE; 
		for (int i = 0; i < probabilities.length; i++) {
			double[] entry = probabilities[i];
			if (currentAge <= entry[0]) {
				if (initProb[i] < entry[2]) {
					// Uniformly assign a random age at event within the current period
					ageAtEvent = entry[0] + Math.random() * (entry[1] - entry[0]) - currentAge;
					break;
				}
			}
			// In case the age of the individual is included within the current interval
			else if (currentAge < entry[1]) {
				if (initProb[i] < entry[2]) {
					// Uniformly assign a random age at event within the current period, taking
					// into account the current age of the individual
					ageAtEvent = Math.random() * (entry[1] - currentAge);
					break;
				}				
			}
		}
		return ageAtEvent;
	}
		
	@SuppressWarnings("unused")
	private static void testGetTimeToEvent(int n, double[][] probabilities, Random rng) {
		int []contador1 = new int[probabilities.length+1];
		for (int i = 0; i < n; i++) {
			double []rnd = new double[probabilities.length];
			for (int j = 0; j <probabilities.length; j++)
				rnd[j]= rng.nextDouble();
			double time = getTimeToEvent(probabilities, 40, rnd) + 40;
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
