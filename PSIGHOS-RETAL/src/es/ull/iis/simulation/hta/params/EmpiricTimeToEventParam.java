/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Random;

import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class EmpiricTimeToEventParam extends Param {
	/** The time unit this parameter is expressed in */
	final protected TimeUnit unit;

	/**
	 * 
	 */
	public EmpiricTimeToEventParam(boolean baseCase, TimeUnit unit) {
		super (baseCase);
		this.unit = unit;
	}

	/**
	 * Initializes the probability arrays according to how they are defined
	 * @param source The original probabilities, expressed either as probabilities or numerator and denominator. 
	 * Positions 0 and 1 contains lower and upper age; position 3 contains the probability or the numerator; position 4 contains the denominator (when applies)
	 * @param destination The destination array of probabilities. Positions 0 and 1 contains lower and upper age; position 3 contains the probability 
	 */
	protected static void initProbabilities(double [][] source, double[][] destination) {
		// The source is originally expressed as a probability
		if (source[0].length == 3) {
			for (int i = 0; i < source.length; i++) {
				destination[i][0] = source[i][0];
				destination[i][1] = source[i][1];
				destination[i][2] = source[i][2];
			}
		}
		// The source is originally expressed as numerator and denominator
		else {
			for (int i = 0; i < source.length; i++) {
				destination[i][0] = source[i][0];
				destination[i][1] = source[i][1];
				destination[i][2] = source[i][2] / source[i][3];
			}
		}
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
	protected static double getTimeToEvent(double[][] probabilities, double currentAge, double []initProb) {
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
