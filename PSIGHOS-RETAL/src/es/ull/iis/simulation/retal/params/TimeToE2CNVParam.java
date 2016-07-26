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
public class TimeToE2CNVParam extends TimeToEventParam {
	private final static Random RNG_E2CNV_E1EARM = new Random();
	private final static double[][] P_E2CNV_E1EARM = {
			{60, 65, 0.014269706},
			{65, 70, 0.021893842},
			{70, CommonParams.MAX_AGE, 0.024646757}
	};
	
	private final static Random RNG_E2CNV_E1GA = new Random();
	private final static double[][] P_E2CNV_E1GA = { 
			{60, 65, 0.0167547991254577},   
			{65, 70, 0.0254060050469566},   
			{70, CommonParams.MAX_AGE, 0.0287673496030733} 
	};

	private final static Random RNG_E2CNV_E1CNV = new Random();
	private final static double[][] P_E2CNV_E1CNV = { 
			{60, 65, 0.0492981931784431},   
			{65, 70, 0.0762051095873376},   
			{70, CommonParams.MAX_AGE, 0.0851567190055321} 
	};

	/** Probability of developing GA in fellow eye, given the state of the first eye */
	private final EnumMap<EyeState, double[][]> pE2CNV = new EnumMap<EyeState, double[][]>(EyeState.class);

	/**
	 * @param baseCase
	 */
	public TimeToE2CNVParam(boolean baseCase) {
		super(baseCase);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of fellow-eye developing CNV given EARM in first eye
		pE2CNV.put(EyeState.EARM, new double[P_E2CNV_E1EARM.length][3]);
		initProbabilities(P_E2CNV_E1EARM, pE2CNV.get(EyeState.EARM));
		// Initialize probability of fellow-eye developing CNV given CNV in first eye
		pE2CNV.put(EyeState.AMD_CNV, new double[P_E2CNV_E1CNV.length][3]);
		initProbabilities(P_E2CNV_E1CNV, pE2CNV.get(EyeState.AMD_CNV));
		// Initialize probability of fellow-eye developing CNV given GA in first eye
		pE2CNV.put(EyeState.AMD_GA, new double[P_E2CNV_E1GA.length][3]);
		initProbabilities(P_E2CNV_E1GA, pE2CNV.get(EyeState.AMD_GA));
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.TimeToEventParam#getTimeToEvent(double)
	 */
	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		
		// First eye has CNV
		if (firstEye.contains(EyeState.AMD_CNV)) {
			double []rnd = new double[P_E2CNV_E1CNV.length];
			for (int j = 0; j < P_E2CNV_E1CNV.length; j++)
				rnd[j] = RNG_E2CNV_E1CNV.nextDouble();
			return getTimeToEvent(pE2CNV.get(EyeState.AMD_CNV), age, rnd);			
		}
		// First eye has GA
		else if (firstEye.contains(EyeState.AMD_GA)) {
			double []rnd = new double[P_E2CNV_E1GA.length];
			for (int j = 0; j < P_E2CNV_E1GA.length; j++)
				rnd[j] = RNG_E2CNV_E1GA.nextDouble();
			return getTimeToEvent(pE2CNV.get(EyeState.AMD_GA), age, rnd);
		}
		// First eye has EARM
		else if (firstEye.contains(EyeState.EARM)) {
			double []rnd = new double[P_E2CNV_E1EARM.length];
			for (int j = 0; j < P_E2CNV_E1EARM.length; j++)
				rnd[j] = RNG_E2CNV_E1EARM.nextDouble();
			return getTimeToEvent(pE2CNV.get(EyeState.EARM), age, rnd);
		}
		// First eye has no ARM
		// FIXME: Change when second order is implemented?
		return Double.MAX_VALUE;
	}

}
