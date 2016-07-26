/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author Iván Castilla
 *
 */
public class TimeToE2GAParam extends TimeToEventParam {
	private final static Random RNG_E2GA_E1EARM = new Random();
	private final static double[][] P_E2GA_E1EARM = {
			{60, 65, 0.049910436},
			{65, 70, 0.060853865},
			{70, CommonParams.MAX_AGE, 0.079085662}
	};
	
	private final static Random RNG_E2GA_E1GA = new Random();
	private final static double[][] P_E2GA_E1GA = { 
			{60, 65, 0.0538734777861744},   
			{65, 70, 0.0667204941316263},   
			{70, CommonParams.MAX_AGE, 0.0850931858956147} 
	};

	private final static Random RNG_E2GA_E1CNV = new Random();
	private final static double[][] P_E2GA_E1CNV = { 
			{60, 65, 0.00591561395661435},   
			{65, 70, 0.00706997998864217},   
			{70, CommonParams.MAX_AGE, 0.00925261820611631} 
	};


	/** Probability of developing GA in fellow eye, given the state of the first eye */
	private final EnumMap<EyeState, double[][]> pE2GA = new EnumMap<EyeState, double[][]>(EyeState.class);

	/**
	 * @param baseCase
	 */
	public TimeToE2GAParam(boolean baseCase) {
		super(baseCase);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of fellow-eye developing GA given EARM in first eye
		pE2GA.put(EyeState.EARM, new double[P_E2GA_E1EARM.length][3]);
		initProbabilities(P_E2GA_E1EARM, pE2GA.get(EyeState.EARM));
		// Initialize probability of fellow-eye developing GA given CNV in first eye
		pE2GA.put(EyeState.AMD_CNV, new double[P_E2GA_E1CNV.length][3]);
		initProbabilities(P_E2GA_E1CNV, pE2GA.get(EyeState.AMD_CNV));
		// Initialize probability of fellow-eye developing GA given GA in first eye
		pE2GA.put(EyeState.AMD_GA, new double[P_E2GA_E1GA.length][3]);
		initProbabilities(P_E2GA_E1GA, pE2GA.get(EyeState.AMD_GA));
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.TimeToEventParam#getTimeToEvent(double)
	 */
	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		
		// First eye has CNV
		if (firstEye.contains(EyeState.AMD_CNV)) {
			double []rnd = new double[P_E2GA_E1CNV.length];
			for (int j = 0; j < P_E2GA_E1CNV.length; j++)
				rnd[j] = RNG_E2GA_E1CNV.nextDouble();
			return getTimeToEvent(pE2GA.get(EyeState.AMD_CNV), age, rnd);			
		}
		// First eye has GA
		else if (firstEye.contains(EyeState.AMD_GA)) {
			double []rnd = new double[P_E2GA_E1GA.length];
			for (int j = 0; j < P_E2GA_E1GA.length; j++)
				rnd[j] = RNG_E2GA_E1GA.nextDouble();
			return getTimeToEvent(pE2GA.get(EyeState.AMD_GA), age, rnd);
		}
		// First eye has EARM
		else if (firstEye.contains(EyeState.EARM)) {
			double []rnd = new double[P_E2GA_E1EARM.length];
			for (int j = 0; j < P_E2GA_E1EARM.length; j++)
				rnd[j] = RNG_E2GA_E1EARM.nextDouble();
			return getTimeToEvent(pE2GA.get(EyeState.EARM), age, rnd);
		}
		// First eye has no ARM
		// FIXME: Change when second order is implemented?
		return Double.MAX_VALUE;
	}

}
