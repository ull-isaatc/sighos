/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.ArrayList;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * @author Iván Castilla
 *
 */
//TODO: Adapt to first and second order as in the rest of similar classes
public class VAProgressionForCNV extends VAProgressionParam {
	// Source: conversion using table from Patel, P.J. et al., 2008. 
	// Intersession repeatability of visual acuity scores in age-related macular degeneration. Investigative Ophthalmology and Visual Science, 49(10), pp.4347–4352.
	private final double LOGMAR_MINUS_15_LETTERS = 0.3;
	
	// Source: Cummulative probabilities from Hurley, S.F., Matthews, J.P. & Guymer, R.H., 2008. Cost-effectiveness of ranibizumab for neovascular age-related macular degeneration. Cost effectiveness and resource allocation : C/E, 6, p.12. 
	private final double[][] untreatedYearlyProgression = {{0.05, 0.285, 0.428}, {0, 0.07, 0.152}};
	private final double[][] ranibizumabYearlyProgression = {{0.338, 0.38, 0.392}, {0.03, 0.081, 0.098}};
	private final double[] logMARChanges = {-LOGMAR_MINUS_15_LETTERS, LOGMAR_MINUS_15_LETTERS, 2*LOGMAR_MINUS_15_LETTERS};

	/**
	 * @param baseCase
	 */
	public VAProgressionForCNV(boolean baseCase) {
		super(baseCase);
	}

	private double getVAChange(Patient pat, double[] probabilities) {
		final double rnd = pat.draw(RandomForPatient.ITEM.ARMD_PROG_CNV);
		for (int i = 0; i < logMARChanges.length; i++) {
			if (rnd <= probabilities[i])
				return logMARChanges[i];
		}
		// No change
		return 0.0;
	}

	private ArrayList<VAProgressionPair> modelProgression(Patient pat, double[][] progression, long startStageTs, long startTs, long endTs, double currentVA, double expectedVA) {
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		// Computes how long of the evaluated period is the patient affected by first year progression rates
		final long timeInYear1Progression = (startTs - startStageTs >= 365) ? 0 : (Math.min(startStageTs + 365, endTs) - startTs);
		// Computes the remaining time, which is affected by second year progression rates
		final double timeInYear2Progression = (endTs - startTs - timeInYear1Progression) / 365.0;
		double va = currentVA;

		if (timeInYear1Progression == 0 && (timeInYear2Progression == 0.0)) {
			pat.error("Progression CNV. Both times should not be 0");
		}
		
		// Progression during first year after starting in this stage (if valid)
		if (timeInYear1Progression > 0) {
			// Calculate the proportional VA loss/gain
			va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(pat, progression[0]) * timeInYear1Progression / 365.0));			
			changes.add(new VAProgressionPair(timeInYear1Progression, va));
		}
		// Progression during second year and on after starting in this stage (if valid)
		if (timeInYear2Progression > 0.0) {
			final int exactYearsSinceEvent = (int)timeInYear2Progression;
			long timeToChange = 0;
			// More than a year since last va change
			if (timeInYear2Progression >= 1.0) {
				final long yearConstant = pat.getSimulation().getTimeUnit().convert(1, TimeUnit.YEAR);
				int i = 0;
				for (; (i < exactYearsSinceEvent) && (va < VisualAcuity.MAX_LOGMAR); i++) {
					timeToChange += yearConstant;
					double vaChange = getVAChange(pat, progression[1]);
					if (vaChange != 0.0) {
						va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(0.0, va + vaChange));
						changes.add(new VAProgressionPair(timeToChange, va));
						timeToChange = 0;
					}
				}
				// If I get to the MAX_LOGMAR before the end of the period
				if (i < exactYearsSinceEvent) {
					timeToChange = (exactYearsSinceEvent - i) * yearConstant; 
				}		
			}
			// Sees if there is an additional change in the remaining time (less than a year)
			timeToChange += pat.getSimulation().getTimeUnit().convert(timeInYear2Progression - exactYearsSinceEvent, TimeUnit.YEAR);
			if (timeToChange > 0) {
				// Calculate the proportional VA loss/gain
				va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(pat, progression[1]) * (timeInYear2Progression - exactYearsSinceEvent)));
				changes.add(new VAProgressionPair(timeToChange, va));	
			}
			if (timeToChange == 0 && changes.size() == 0)
				pat.error("Progression CNV. Badly composed");
		}
		if (changes.size() == 0)
			pat.error("Progression CNV. Badly composed");
		return changes;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.VAProgressionParam#getVAProgression(es.ull.iis.simulation.retal.Patient, int, double)
	 */
	@Override
	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, double expectedVA) {
		double va = pat.getVA(eyeIndex);
		final long startTs = pat.getLastVAChangeTs(eyeIndex); 
		final long startStageTs = pat.getTimeToCNVStage(pat.getCurrentCNVStage(eyeIndex), eyeIndex);
		final long endTs = pat.getSimulation().getTs();
		final TimeUnit simUnit = pat.getSimulation().getTimeUnit();
		// TODO: Check that making treatment effective only first two years is working fine
		final ArrayList<VAProgressionPair> changes;
		if (pat.isDiagnosed()) {
			if (CommonParams.ANTIVEGF_2YEARS_ASSUMPTION) {
				final long twoYearsSinceAntiVEGF = simUnit.convert(2, TimeUnit.YEAR) + pat.getOnAntiVEGFCNV(eyeIndex);
				if (startTs >= twoYearsSinceAntiVEGF) {
					changes = modelProgression(pat, untreatedYearlyProgression, startStageTs, startTs, endTs, va, expectedVA);
				}
				else if (endTs <= twoYearsSinceAntiVEGF) {
					changes = modelProgression(pat, ranibizumabYearlyProgression, pat.getOnAntiVEGFCNV(eyeIndex), startTs, endTs, va, va);
				}
				else {
					changes = modelProgression(pat, ranibizumabYearlyProgression, pat.getOnAntiVEGFCNV(eyeIndex), startTs, twoYearsSinceAntiVEGF, va, va);
					changes.addAll(modelProgression(pat, untreatedYearlyProgression, startStageTs, twoYearsSinceAntiVEGF, endTs, changes.get(changes.size() - 1).va, expectedVA));
				}
			}
			// Assumming no time limit in effectiveness
			else {
				// Do not use expected VA if on treatment
				changes = modelProgression(pat, ranibizumabYearlyProgression, pat.getOnAntiVEGFCNV(eyeIndex), startTs, endTs, va, va);
			}
		}
		else {
			changes = modelProgression(pat, untreatedYearlyProgression, startStageTs, startTs, endTs, va, expectedVA);
		}
		return changes;
	}

}
