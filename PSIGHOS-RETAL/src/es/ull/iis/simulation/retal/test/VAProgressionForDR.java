/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class VAProgressionForDR {
	/** 
	 * Mean and SD VA for diabetic retinopathy. 
	 * Source: Agardh, E., Hellgren, K.J. & Bengtsson, B., 2011. Stable refraction and visual acuity in diabetic patients with 
	 * variable glucose levels under routine care. Acta Ophthalmologica, 89(2), pp.107–110.
	 */
	private final static double[] MEAN_SD_VA_DR = {0.08, 0.05};
	/** Annual probability and visual acuity loss for eyes affected by non-HR PDR (independently of being affected by CSME).
	 * Source: Rein */
	private final static double[] PROB_VA_NONHR_PDR = {0.9, 0.0356};
	/** Annual probability and visual acuity loss for eyes affected by HR PDR (independently of being affected by CSME) 
	 * Source: Rein */
	private final static double[] PROB_VA_HR_PDR = {0.9, 0.1409};
	/** Annual probability and visual acuity loss for eyes affected by (only) CSME 
	 * Source: Rein */
	private final static double[] PROB_VA_CSME = {0.3, 0.1056};

	private final RandomVariate initialDR;

	/**
	 * @param simul
	 * @param baseCase
	 */
	public VAProgressionForDR() {
		initialDR = RandomVariateFactory.getInstance("NormalVariate", MEAN_SD_VA_DR[0], MEAN_SD_VA_DR[1]);
	}

	/**
	 * Returns the initial VA when the eye is already affected.
	 * A priori, it should be used only for DR
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	public double getInitialVA() {
		final double value = initialDR.generate();
		return (value < 0.0) ? 0.0 : value;
	}
	
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.VAProgressionParam#getVAProgression(es.ull.iis.simulation.retal.Patient, int, double)
	 */
	public ArrayList<VAProgressionPair> getVAProgression(long currentTs, long lastChangeTs, EnumSet<EyeState> eyeState, double currentVA, double expectedVA) {
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		final long daysSinceLastChange = currentTs - lastChangeTs;
		if (eyeState.contains(EyeState.NPDR)) {
			changes.add(new VAProgressionPair(daysSinceLastChange, expectedVA));
		}
		else {
			double va = currentVA;
			
			final Random rnd = new Random();
			final double[] probVA;
			if (eyeState.contains(EyeState.NON_HR_PDR)) {
				probVA = PROB_VA_NONHR_PDR;
			}
			else if (eyeState.contains(EyeState.HR_PDR)) {
				probVA = PROB_VA_HR_PDR;
			}
			else if (eyeState.contains(EyeState.CSME)) {
				probVA = PROB_VA_CSME;
			}
			else {
				System.err.println("Trying to calculate VA loss from DR with a patient who has not DR");
				probVA = new double[2];
			}
			long timeToChange = 0;
			// More than a year since last change
			if (daysSinceLastChange >= 365) {
				final long yearConstant = TimeUnit.DAY.convert(1, TimeUnit.YEAR);
				for (int i = 0; i < daysSinceLastChange/365; i++) {
					timeToChange += yearConstant;
					if (rnd.nextDouble() < probVA[0]) {
						va = Math.min(VisualAcuity.MAX_LOGMAR, va + probVA[1]);
						changes.add(new VAProgressionPair(timeToChange, va));
						timeToChange = 0;
					}
				}
			}
			// Sees if there is an additional change in the remaining time (less than a year)
			timeToChange += (daysSinceLastChange - (daysSinceLastChange / 365) * 365);
			if (rnd.nextDouble() < probVA[0]) {
				va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(va + probVA[1], expectedVA));
			}
			// Adds the last change
			changes.add(new VAProgressionPair(timeToChange, va));
		}
			
		return changes;
	}

}
