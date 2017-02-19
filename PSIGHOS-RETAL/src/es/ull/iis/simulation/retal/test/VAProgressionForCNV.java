/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.ArrayList;
import java.util.Random;

import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.retal.params.CommonParams;

/**
 * @author Iván Castilla
 *
 */
//TODO: Adapt to first and second order as in the rest of similar classes
public class VAProgressionForCNV {
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
	public VAProgressionForCNV() {
	}

	private double getVAChange(double rnd, double[] probabilities) {
		for (int i = 0; i < logMARChanges.length; i++) {
			if (rnd <= probabilities[i])
				return logMARChanges[i];
		}
		// No change
		return 0.0;
	}
	
	public ArrayList<VAProgressionPair> getOldVAProgression(long currentTs, long lastChangeTs, double currentVA, CNVStage stage, boolean diagnosed, double expectedVA) {
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		double va = currentVA;
		final double yearsSinceEvent = (currentTs - lastChangeTs) / 365.0;
		final int exactYearsSinceEvent = (int)yearsSinceEvent;
		final double[][] progression;
		if (diagnosed) {
			progression = ranibizumabYearlyProgression;
			// Do not use expected VA if on treatment
			expectedVA = va;
		}
		else {
			progression = untreatedYearlyProgression;
		}
		
		final Random rng = new Random();
		long timeToChange = 0;
		// More than a year since last change
		if (yearsSinceEvent > 1.0) {
			final long yearConstant = 365;
			// Check change for year 1
			double vaChange = getVAChange(rng.nextDouble(), progression[0]);
			if (vaChange != 0.0) {
				va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(0.0, va + vaChange));
				changes.add(new VAProgressionPair(yearConstant, va));
			}
			else {
				timeToChange += yearConstant;
			}
			// Check changes for year 2 and on
			if (yearsSinceEvent > 2.0) {
				int i = 1;
				for (; (i < exactYearsSinceEvent) && (va < VisualAcuity.MAX_LOGMAR); i++) {
					timeToChange += yearConstant;
					vaChange = getVAChange(rng.nextDouble(), progression[1]);
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
			else {
				timeToChange += yearConstant;
			}
			// Sees if there is an additional change in the remaining time (less than a year)
			timeToChange += TimeUnit.DAY.convert(yearsSinceEvent - exactYearsSinceEvent, TimeUnit.YEAR);
			// Calculate the proportional VA loss/gain
			va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(rng.nextDouble(), progression[1]) * (yearsSinceEvent - exactYearsSinceEvent)));
		}
		// Less than a year since last change
		else {
			timeToChange = currentTs - lastChangeTs;
			// Calculate the proportional VA loss/gain
			va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(rng.nextDouble(), progression[0]) * yearsSinceEvent));
		}
		// Adds the last change
		if (timeToChange > 0)
			changes.add(new VAProgressionPair(timeToChange, va));
		return changes;
	}

	private ArrayList<VAProgressionPair> modelProgression(double[][] progression, long startStageTs, long startTs, long endTs, double currentVA, double expectedVA) {
		final Random rng = new Random();
		final ArrayList<VAProgressionPair> changes = new ArrayList<VAProgressionPair>();
		// Computes how long of the evaluated period is the patient affected by first year progression rates
		final long timeInYear1Progression = (startTs - startStageTs >= 365) ? 0 : (Math.min(startStageTs + 365, endTs) - startTs);
		// Computes the remaining time, which is affected by second year progression rates
		final double timeInYear2Progression = (endTs - startTs - timeInYear1Progression) / 365.0;
		double va = currentVA;

		// Progression during first year after starting in this stage (if valid)
		if (timeInYear1Progression > 0) {
			// Calculate the proportional VA loss/gain
			va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(rng.nextDouble(), progression[0]) * timeInYear1Progression / 365.0));			
			changes.add(new VAProgressionPair(timeInYear1Progression, va));
		}
		// Porgression during second year and on after starting in this stage (if valid)
		if (timeInYear2Progression > 0.0) {
			final int exactYearsSinceEvent = (int)timeInYear2Progression;
			long timeToChange = 0;
			// More than a year since last va change
			if (timeInYear2Progression >= 1.0) {
				final long yearConstant = TimeUnit.DAY.convert(1, TimeUnit.YEAR);
				int i = 0;
				for (; (i < exactYearsSinceEvent) && (va < VisualAcuity.MAX_LOGMAR); i++) {
					timeToChange += yearConstant;
					double vaChange = getVAChange(rng.nextDouble(), progression[1]);
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
			timeToChange += TimeUnit.DAY.convert(timeInYear2Progression - exactYearsSinceEvent, TimeUnit.YEAR);
			if (timeToChange > 0) {
				// Calculate the proportional VA loss/gain
				va = Math.min(VisualAcuity.MAX_LOGMAR, Math.max(expectedVA, va + getVAChange(rng.nextDouble(), progression[1]) * (timeInYear2Progression - exactYearsSinceEvent)));
				changes.add(new VAProgressionPair(timeToChange, va));	
			}
		}
		return changes;
	}
	
	public ArrayList<VAProgressionPair> getVAProgression(long startTs, long endTs, long startStageTs, long onAntiVEGFTs, double currentVA, boolean diagnosed, double expectedVA) {
		double va = currentVA;
		final TimeUnit simUnit = TimeUnit.DAY;
		// TODO: Check that making treatment effective only first two years is working fine
		final ArrayList<VAProgressionPair> changes;
		if (diagnosed) {
			if (CommonParams.ANTIVEGF_2YEARS_ASSUMPTION) {
				final long twoYearsSinceAntiVEGF = simUnit.convert(2, TimeUnit.YEAR) + onAntiVEGFTs;
				if (startTs >= twoYearsSinceAntiVEGF) {
					changes = modelProgression(untreatedYearlyProgression, startStageTs, startTs, endTs, va, expectedVA);
				}
				else if (endTs <= twoYearsSinceAntiVEGF) {
					changes = modelProgression(ranibizumabYearlyProgression, onAntiVEGFTs, startTs, endTs, va, va);
				}
				else {
					changes = modelProgression(ranibizumabYearlyProgression, onAntiVEGFTs, startTs, twoYearsSinceAntiVEGF, va, va);
					changes.addAll(modelProgression(untreatedYearlyProgression, startStageTs, twoYearsSinceAntiVEGF, endTs, changes.get(changes.size() - 1).va, expectedVA));
				}
			}
			// Assumming no time limit in effectiveness
			else {
				// Do not use expected VA if on treatment
				changes = modelProgression(ranibizumabYearlyProgression, onAntiVEGFTs, startTs, endTs, va, va);
			}
		}
		else {
			changes = modelProgression(untreatedYearlyProgression, startStageTs, startTs, endTs, va, expectedVA);
		}
		return changes;
	}

}
