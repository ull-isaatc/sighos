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
	private final double LOGMAR_MINUS_15_LETTERS = 0.30103;
	
	// Source: Cummulative probabilities from Hurley, S.F., Matthews, J.P. & Guymer, R.H., 2008. Cost-effectiveness of ranibizumab for neovascular age-related macular degeneration. Cost effectiveness and resource allocation : C/E, 6, p.12. 
	private final double[] untreated1YearProgression = {0.05, 0.622, 0.857, 1};
	private final double[] untreated2YearsProgression = {0, 0.848, 0.918, 1};
	private final double[] ranibizumab1YearProgression = {0.338, 0.946, 0.988, 1};
	private final double[] ranibizumab2YearsProgression = {0.03, 0.932, 0.983, 1};
	private final double[] logMARChanges = {-LOGMAR_MINUS_15_LETTERS, 0.0, LOGMAR_MINUS_15_LETTERS, 2*LOGMAR_MINUS_15_LETTERS};

	/**
	 * @param baseCase
	 */
	public VAProgressionForCNV(boolean baseCase) {
		super(baseCase);
	}

	private double getVAChange(Patient pat, double[] probabilities) {
		final double rnd = pat.draw(RandomForPatient.ITEM.ARMD_PROG_CNV);
		for (int i = 0; i < logMARChanges.length - 1; i++) {
			if (rnd <= probabilities[i])
				return logMARChanges[i];
		}
		return logMARChanges[logMARChanges.length - 1];
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.VAProgressionParam#getVAProgression(es.ull.iis.simulation.retal.Patient, int, double)
	 */
	@Override
	public ArrayList<VAProgressionPair> getVAProgression(Patient pat, int eyeIndex, double expectedVA) {
		final ArrayList<VAProgressionPair> array = new ArrayList<VAProgressionPair>();
		final CNVStage stage = pat.getCurrentCNVStage(eyeIndex);
		final double currentVA = pat.getVA(eyeIndex);
		double yearsSinceEvent = TimeUnit.DAY.convert(pat.getSimulation().getTs() - pat.getTimeToCNVStage(stage, eyeIndex), pat.getSimulation().getTimeUnit()) / 365.0;
		if (yearsSinceEvent > 1.0) {
			
		}
		return null;
	}

}
