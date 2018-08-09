/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
// FIXME: Do take effectiveness of treatments from Rein! First check if treatment for CSME is antiVEGF or PFC
public class VAProgressionForDR extends VAProgressionParam {
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

	private final static double LOSS_RATE_TREATED_NONHR_PDR = 0.4571;
	private final static double LOSS_RATE_TREATED_HR_PDR = 0.4160;
	private final static double LOSS_RATE_TREATED_CSME = 0.3750;
	
	private final RandomVariate initialDR;

	/**
	 * @param simul
	 * @param secondOrder
	 */
	public VAProgressionForDR() {
		super();
		initialDR = RandomVariateFactory.getInstance("NormalVariate", RandomForPatient.getRandomNumber(RandomForPatient.ITEM.DR_INITIAL_VA), MEAN_SD_VA_DR[0], MEAN_SD_VA_DR[1]);
	}

	/**
	 * Returns the initial VA when the eye is already affected.
	 * A priori, it should be used only for DR
	 * @param pat A patient
	 * @param eyeIndex Index of the affected eye (0 for first eye, 1 for second eye)
	 * @return
	 */
	public double getInitialVA(RetalPatient pat, int eyeIndex) {
		final double value = initialDR.generate();
		return (value < 0.0) ? 0.0 : value;
	}
	
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.retal.params.VAProgressionParam#getVAProgression(es.ull.iis.simulation.hta.retal.RetalPatient, int, double)
	 */
	@Override
	public ArrayList<VAProgressionPair> getVAProgression(RetalPatient pat, int eyeIndex, double expectedVA) {
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		final long timeSinceLastChange = pat.getSimulation().getTs() - pat.getLastVAChangeTs(eyeIndex);
		if (pat.getEyeState(eyeIndex).contains(EyeState.NPDR)) {
			changes.add(new VAProgressionPair(timeSinceLastChange, expectedVA));
		}
		else {
			final int yearsSinceLastChange = (int)(TimeUnit.DAY.convert(timeSinceLastChange, pat.getSimulation().getTimeUnit()) / 365);
			double va = pat.getVA(eyeIndex);
			
			final double[] rnd;
			final double[] probVA;
			final double lossRate;
			if (pat.getEyeState(eyeIndex).contains(EyeState.NON_HR_PDR)) {
				rnd = pat.draw(RandomForPatient.ITEM.DR_VA_NONHR_PDR, yearsSinceLastChange + 1);
				probVA = PROB_VA_NONHR_PDR;
				lossRate = LOSS_RATE_TREATED_NONHR_PDR;
			}
			else if (pat.getEyeState(eyeIndex).contains(EyeState.HR_PDR)) {
				rnd = pat.draw(RandomForPatient.ITEM.DR_VA_HR_PDR, yearsSinceLastChange + 1);
				probVA = PROB_VA_HR_PDR;
				lossRate = LOSS_RATE_TREATED_HR_PDR;
			}
			else if (pat.getEyeState(eyeIndex).contains(EyeState.CSME)) {
				rnd = pat.draw(RandomForPatient.ITEM.DR_VA_CSME, yearsSinceLastChange + 1);
				probVA = PROB_VA_CSME;
				lossRate = LOSS_RATE_TREATED_CSME;
			}
			else {
				pat.error("Trying to calculate VA loss from DR with a patient who has not DR");
				probVA = new double[2];
				rnd = new double[yearsSinceLastChange + 1];
				lossRate = 1.0;
			}
			if (pat.isDiagnosed()) {
				probVA[0] *= lossRate; 
			}
			long timeToChange = 0;
			// More than a year since last change
			if (yearsSinceLastChange > 0) {
				final long yearConstant = pat.getSimulation().getTimeUnit().convert(1, TimeUnit.YEAR);
				int i = 0;
				for (; (i < yearsSinceLastChange) && (va < VisualAcuity.MAX_LOGMAR); i++) {
					timeToChange += yearConstant;
					if (rnd[i] < probVA[0]) {
						va = Math.min(VisualAcuity.MAX_LOGMAR, va + probVA[1]);
						changes.add(new VAProgressionPair(timeToChange, va));
						timeToChange = 0;
					}
				}
				if (i < yearsSinceLastChange) {
					timeToChange = (yearsSinceLastChange - i) * yearConstant; 
				}					
			}
			// Sees if there is an additional change in the remaining time (less than a year)
			timeToChange += (timeSinceLastChange - pat.getSimulation().getTimeUnit().convert(yearsSinceLastChange, TimeUnit.YEAR));
			if (rnd[rnd.length - 1] < probVA[0]) {
				va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(va + probVA[1], expectedVA));
			}
			// Adds the last change
			changes.add(new VAProgressionPair(timeToChange, va));
		}
			
		return changes;
	}

}
