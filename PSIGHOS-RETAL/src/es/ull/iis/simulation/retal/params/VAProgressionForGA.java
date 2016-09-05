/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
//FIXME: Adapt to first and second order as in the rest of similar classes
public class VAProgressionForGA extends VAProgressionParam {
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

	@Override
	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, double expectedVA) {
		final ArrayList<VAProgressionPair> array = new ArrayList<VAProgressionPair>();
		final double currentVA = pat.getVA(eyeIndex);
		final double rnd = pat.draw(RandomForPatient.ITEM.ARMD_PROG_GA);
		int year = yearlyProgression.length;
		for (int i = 0; (i < yearlyProgression.length) && (year == yearlyProgression.length); i++) {
			if (rnd < yearlyProgression[i])
				year = i;
		}
		final long timeSinceGA = simul.getTs() - pat.getTimeToEyeState(EyeState.AMD_GA, eyeIndex);
		// If no changes in four years, it is supposed not to progress
		if (year == yearlyProgression.length) {
			array.add(new VAProgressionPair(timeSinceGA, expectedVA));
		}
		else {
			// Else, checks how much this patient progresses 
			double incVA = (pat.draw(RandomForPatient.ITEM.ARMD_PROG_TWICE_GA) < PROP_PROGRESSING_TWICE) ? LOSS_6_LINES : LOSS_3_LINES;
			long time = simul.getTimeUnit().convert(year + 1, TimeUnit.YEAR);
			// If the change will happen in the future, performs a linear interpolation to estimate the new VA
			if (timeSinceGA <= time) {
				incVA = incVA * timeSinceGA / time;
				time = timeSinceGA;
			}
			// If the change happens within the period that the patient had GA
			else if (timeSinceGA > time) {
				array.add(new VAProgressionPair(time, Math.min(VisualAcuity.MAX_LOGMAR, currentVA + incVA)));
				time = timeSinceGA - time;
			}
			// Uses the expected VA if it's higher than the computed VA
			array.add(new VAProgressionPair(time, Math.min(VisualAcuity.MAX_LOGMAR, Math.max(currentVA + incVA, expectedVA))));
		}
		return array;
	}
}
