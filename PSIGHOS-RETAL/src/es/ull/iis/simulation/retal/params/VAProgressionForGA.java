/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
//TODO: Adapt to first and second order as in the rest of similar classes
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
	public VAProgressionForGA(boolean baseCase) {
		super(baseCase);
		for (int i = 0; i < VA_PROGRESSION.length; i++)
			yearlyProgression[i] = VA_PROGRESSION[i][0];
	}

	@Override
	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, double expectedVA) {
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		final double rnd = pat.draw(RandomForPatient.ITEM.ARMD_PROG_GA);
		final long startTs = pat.getLastVAChangeTs(eyeIndex); 
		final long startGATs = pat.getTimeToEyeState(EyeState.AMD_GA, eyeIndex);
		final long endTs = pat.getSimulationEngine().getTs();
		final long daysSinceGA = startTs - startGATs;
		final long fourYearsInDays = yearlyProgression.length * 365;
		final long period = endTs - startTs;

		// More than four years since GA onset... no changes but the expected va
		if (daysSinceGA >= fourYearsInDays) {
			changes.add(new VAProgressionPair(period, expectedVA));
		}
		else {
			double va = pat.getVA(eyeIndex);
			final int yearsSinceGA = (int)(daysSinceGA / 365.0);
			int year = yearlyProgression.length;
			// See when it is supposed to change the VA
			for (int i = 0; (i < yearlyProgression.length) && (year == yearlyProgression.length); i++) {
				if (rnd < yearlyProgression[i]) {
					year = i;
				}
			}
			// No change
			if (year == yearlyProgression.length) {
				changes.add(new VAProgressionPair(period, expectedVA));
			}
			// Supposed to happen in the past... Ignore it
			else if (year < yearsSinceGA) {
				changes.add(new VAProgressionPair(period, expectedVA));
			}
			// Supposed to happen in the first year of the period being analized
			else if (year == yearsSinceGA) {
				final long timeToChange = Math.min(period, 365 - daysSinceGA % 365);
				if (timeToChange > 0) {
					// Checks how much this patient progresses 
					final double incVA = (pat.draw(RandomForPatient.ITEM.ARMD_PROG_TWICE_GA) < PROP_PROGRESSING_TWICE) ? LOSS_6_LINES : LOSS_3_LINES;
					// Apply only proportional loss
					va = Math.min(VisualAcuity.MAX_LOGMAR, va + incVA * (timeToChange / 365.0));
					changes.add(new VAProgressionPair(timeToChange, va));
				}
				// Schedules a new event for the remaining of the period
				if (period > timeToChange) {
					changes.add(new VAProgressionPair(period - timeToChange, Math.max(expectedVA, va)));
				}
				if (changes.size() == 0) {
					// FIXME: Bad idea: possibly masking errors...
					changes.add(new VAProgressionPair(period, expectedVA));
				}
			}
			else {
				// Checks how much this patient progresses 
				final double incVA = (pat.draw(RandomForPatient.ITEM.ARMD_PROG_TWICE_GA) < PROP_PROGRESSING_TWICE) ? LOSS_6_LINES : LOSS_3_LINES;
				// The change happens in the future
				if ((year + 1) * 365 >= endTs - startGATs) {
					changes.add(new VAProgressionPair(period, Math.max(expectedVA, Math.min(VisualAcuity.MAX_LOGMAR, va + incVA * (endTs - startGATs) / (year * 365)))));
				}
				else {
					final long timeToChange = pat.getSimulationEngine().getTimeUnit().convert(year + 1, TimeUnit.YEAR) - startTs + startGATs;
					if (period > timeToChange) {
						va = Math.min(VisualAcuity.MAX_LOGMAR, va + incVA);
						changes.add(new VAProgressionPair(timeToChange, va));
						changes.add(new VAProgressionPair(period - timeToChange, Math.max(expectedVA, va)));
					}
					else  {
						changes.add(new VAProgressionPair(timeToChange, Math.max(expectedVA, Math.min(VisualAcuity.MAX_LOGMAR, va + incVA))));						
					}
				}
			}
		}
		return changes;
	}
}
