/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
//FIXME: Adapt to first and second order as in the rest of similar classes
public class VAProgressionForGA extends Param {
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
	
	private final double[] yearlyProgression = new double[VA_PROGRESSION.length];

	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAProgressionForGA(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase);
		for (int i = 0; i < VA_PROGRESSION.length; i++)
			yearlyProgression[i] = VA_PROGRESSION[i][0];
	}

	public VAParam.VAProgressionPair[] getProgression(OphthalmologicPatient pat, int eyeIndex) {
		final double currentVA = pat.getVA(eyeIndex);
		final double rnd = pat.getRndProgGA();
		final long timeSinceGA = simul.getTs() - pat.getTimeToGA(eyeIndex);
		int year = yearlyProgression.length;
		for (int i = 0; (i < yearlyProgression.length) && (year == yearlyProgression.length); i++) {
			if (rnd < yearlyProgression[i])
				year = i;
		}
		final long time = simul.getTimeUnit().convert(year + 1, TimeUnit.YEAR);
		if ((year == yearlyProgression.length) || (timeSinceGA < time))
			return null;
		// Checks if this patient progresses twice as fast as the rest
		return new VAParam.VAProgressionPair[] {new VAParam.VAProgressionPair(time, 
				currentVA + ((pat.getRndProgTwiceGA() < PROP_PROGRESSING_TWICE) ? LOSS_6_LINES : LOSS_3_LINES))};
	}
}
