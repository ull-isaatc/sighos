/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
//FIXME: Adapt to first and second order as in the rest of similar classes
class VAProgressionForGA {
	private static final double LOSS_3_LINES = 0.3;
	private static final double LOSS_6_LINES = 0.6;
	/**
	 * Source: Karnon (Table 34 - page 32)
	 */
	private static final double[][] VA_PROGRESSION = {
			{0.15, 0.06, 0.23},
			{0.41, 0.27, 0.52},
			{0.60, 0.44, 0.72},
			{0.70, 0.49, 0.83}
	};
	private static final double PROP_PROGRESSING_TWICE = 1.0/3.0;
	private static Random rnd = new Random();
	
	private final double[] yearlyProgression = new double[VA_PROGRESSION.length];

	/**
	 * @param simul
	 * @param baseCase
	 */
	protected VAProgressionForGA() {
		for (int i = 0; i < VA_PROGRESSION.length; i++)
			yearlyProgression[i] = VA_PROGRESSION[i][0];
	}

	protected VAProgressionPair[] getProgression(double currentVA, long daysFrom) {
		int year = yearlyProgression.length;
		double rn = rnd.nextDouble();
		for (int i = 0; (i < yearlyProgression.length) && (year == yearlyProgression.length); i++) {
			if (rn < yearlyProgression[i])
				year = i;
		}
		if (year == yearlyProgression.length)
			return null;
		double incVA  = (rnd.nextDouble() < PROP_PROGRESSING_TWICE) ? LOSS_6_LINES : LOSS_3_LINES;
		long time = TimeUnit.DAY.convert(year + 1, TimeUnit.YEAR);
		if (daysFrom < time) {
			incVA = incVA * daysFrom / time; 
			time = daysFrom;
		}

		// Checks if this patient progresses twice as fast as the rest
		return new VAProgressionPair[] {new VAProgressionPair(time, currentVA + incVA)};
	}
}
