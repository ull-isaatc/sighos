/**
 * 
 */
package es.ull.iis.simulation.retal.test;

import java.util.ArrayList;
import java.util.Random;

import es.ull.iis.simulation.core.TimeUnit;

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
	
	public ArrayList<VAProgressionPair> getVAProgression(long currentTs, long lastChangeTs, double currentVA, CNVStage stage, boolean diagnosed, double expectedVA) {
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

}
