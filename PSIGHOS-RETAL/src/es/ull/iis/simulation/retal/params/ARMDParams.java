/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ARMDParams extends ModelParams {
	// Parameters for First eye incidence of EARM --> BAD ADJUST!!!!
	private final static Random RNG_EARM = new Random();
//	private final static double ALPHA_EARM = Math.exp(-11.41320441);
//	private final static double BETA_EARM = 0.097865047;
	/** Parameters for an empiric distribution on incidence of EARM. Source: Rotterdam study as stated in Karnon's report (pag 25) */
	private final static double [][] P_EARM = {	
			{55, 60, 3, 2179},
			{60, 65, 32, 6085},
			{65, 70, 69, 6376},
			{70, 75, 97, 5102},
			{75, 80, 102, 3212},
			{80, CommonParams.MAX_AGE, 110, 2159}};

	
	// Parameters for progression to AMD given that FE has no ARM --> Adjust not tested
	private final static Random RNG_ARM2AMD_E2_NOARM = new Random();
//	private final static double ALPHA_ARM2AMD_FE_NOARM = Math.exp(-7.218078288);
//	private final static double BETA_ARM2AMD_FE_NOARM = 0.043182526;
	private final static double [][] P_EARM2AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317, 359},
			{70, 80, 16.6795708705063, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988, 335}};

	private final static double [][] P_CNV = {
			{60, 65, 1, 1},
			{65, 70, 2, 50},
			{70, 75, 7, 10},
			{75, 80, 9, 14},
			{80, CommonParams.MAX_AGE, 9, 17}};

	/** Proportion of CNV (against GA) in first eye incident AMD */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	/** First-eye incidence of EARM */
	private final double [][] pEARM = new double[P_EARM.length][3];	
	
	/** Probability of developing AMD in first eye with EARM, given the state of the fellow eye */ 
	private final EnumMap<EyeState, double[][]> pEARM2AMD = new EnumMap<EyeState, double[][]>(EyeState.class);

	/** Probability of developing GA in fellow eye, given the state of the first eye */
	private final EnumMap<EyeState, double[][]> pE2GA = new EnumMap<EyeState, double[][]>(EyeState.class);
	
	private final TimeToAMDParam timeToAMD;
	private final TimeToE2CNVParam timeToE2CNV;
	private final TimeToE2GAParam timeToE2GA;

	/**
	 * 
	 */
	public ARMDParams(boolean baseCase) {
		super(baseCase);
		timeToAMD = new TimeToAMDParam(baseCase);
		timeToE2CNV = new TimeToE2CNVParam(baseCase);
		timeToE2GA = new TimeToE2GAParam(baseCase);
		
		if (baseCase) {
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
			// Initialize first-eye incidence of EARM
			initProbabilities(P_EARM, pEARM);
			// Initialize probability of first-eye developing AMD from EARM
			pEARM2AMD.put(EyeState.HEALTHY, new double[P_EARM2AMD_E2_NOARM.length][3]);
			initProbabilities(P_EARM2AMD_E2_NOARM, pEARM2AMD.get(EyeState.HEALTHY));
		}
		else {
			// FIXME: Replace by random distributions
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
			// Initialize first-eye incidence of EARM
			initProbabilities(P_EARM, pEARM);
			// Initialize probability of first-eye developing AMD from EARM
			pEARM2AMD.put(EyeState.HEALTHY, new double[P_EARM2AMD_E2_NOARM.length][3]);
			initProbabilities(P_EARM2AMD_E2_NOARM, pEARM2AMD.get(EyeState.HEALTHY));
		}
	}

	/**
	 * 
	 * @param age
	 * @return
	 */
	public double getProbabilityCNV(double age) {
		final Map.Entry<Integer, Double> entry = pCNV.lowerEntry((int)age);
		if (entry != null)
			return entry.getValue();
		return 0.0;
	}

	/**
	 * @return Years to first eye incidence of early ARM; Double.MAX_VALUE if event is not happening
	 */
	public double getEARMTime(double age) {
		final double []rnd = new double[pEARM.length];
		for (int j = 0; j <pEARM.length; j++)
			rnd[j] = RNG_EARM.nextDouble();
		return getTimeToEvent(pEARM, age, rnd);
	}
	
	/**
	 * @return the time to first eye incidence of AMD
	 */
	public double getAMDTime(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToAMD.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param fellowEye
	 * @return the time to first eye progression from ARM to AMD
	 */
	public double getEARM2AMDTime(double age, EnumSet<EyeState> fellowEye) {
		// Fellow eye has CNV
		if (fellowEye.contains(EyeState.AMD_CNV)) {
			// TODO
		}
		// Fellow eye has GA
		else if (fellowEye.contains(EyeState.AMD_GA)) {
			// TODO
		}
		// Fellow eye has EARM
		else if (fellowEye.contains(EyeState.EARM)) {
			// TODO
		}
		// Fellow eye has no ARM
		else {
			double []rnd = new double[P_EARM2AMD_E2_NOARM.length];
			for (int j = 0; j < P_EARM2AMD_E2_NOARM.length; j++)
				rnd[j] = RNG_ARM2AMD_E2_NOARM.nextDouble();
			return getTimeToEvent(pEARM2AMD.get(EyeState.HEALTHY), age, rnd);
		}
		return Double.MAX_VALUE;
	}

	/**
	 * 
	 * @param age
	 * @param fellowEye
	 * @return
	 */
	public double getGA2CNVTime(double age, EnumSet<EyeState> fellowEye) {
		final double result;
		
		// Fellow eye has CNV
		if (fellowEye.contains(EyeState.AMD_CNV)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has GA
		else if (fellowEye.contains(EyeState.AMD_GA)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has EARM
		else if (fellowEye.contains(EyeState.EARM)) {
			// TODO
			result = 0.0;
		}
		// Fellow eye has no ARM
		else {
			// FIXME: Change when second order is implemented
			result = Double.MAX_VALUE;
		}
		return result;
	}

	/**
	 * 
	 * @param age
	 * @param firstEye
	 * @return
	 */
	public double getE2Time2GA(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToE2GA.getTimeToEvent(age, firstEye, fellowEye);
	}

	/**
	 * 
	 * @param age
	 * @param firstEye
	 * @return
	 */
	public double getE2Time2CNV(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		return timeToE2CNV.getTimeToEvent(age, firstEye, fellowEye);
	}
}
