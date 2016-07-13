/**
 * 
 */
package es.ull.iis.simulation.retal;

import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

	// Parameters for first eye incidence of AMD -->  BAD ADJUST!!!!
	private final static Random RNG_AMD = new Random();
//	private final static double ALPHA_AMD = Math.exp(-11.4989645);
//	private final static double BETA_AMD = 0.05463568;
	private final static double [][] P_AMD = {	
			{55, 60, 0, 2179},
			{60, 70, 3.602758243, 12461},
			{70, 80, 6.48455774, 8314},
			{80, CommonParams.MAX_AGE, 3.019364846, 2159}};
	
	// Parameters for progression to AMD given that FE has no ARM --> Adjust not tested
	private final static Random RNG_ARM2AMD_FE_NOARM = new Random();
//	private final static double ALPHA_ARM2AMD_FE_NOARM = Math.exp(-7.218078288);
//	private final static double BETA_ARM2AMD_FE_NOARM = 0.043182526;
	private final static double [][] P_EARM2AMD_FE_NOARM = { 
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
	/** First-eye incidence of AMD */
	private final double [][] pAMD = new double[P_AMD.length][3];
	/** Probability of developing AMD in first eye with EARM, having no ARM in fellow eye */ 
	private final double [][] pEARM2AMD_FE_NOARM = new double[P_EARM2AMD_FE_NOARM.length][3];
	
	/**
	 * 
	 */
	public ARMDParams(boolean baseCase) {
		super(baseCase);
		if (baseCase) {
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
			// Initialize first-eye incidence of EARM
			for (int i = 0; i < P_EARM.length; i++) {
				pEARM[i][0] = P_EARM[i][0];
				pEARM[i][1] = P_EARM[i][1];
				pEARM[i][2] = P_EARM[i][2] / P_EARM[i][3];
			}
			// Initialize first-eye incidence of AMD
			for (int i = 0; i < P_AMD.length; i++) {
				pAMD[i][0] = P_AMD[i][0];
				pAMD[i][1] = P_AMD[i][1];
				pAMD[i][2] = P_AMD[i][2] / P_AMD[i][3];
			}
			// Initialize probability of first-eye development of AMD from EARM
			for (int i = 0; i < P_EARM2AMD_FE_NOARM.length; i++) {
				pEARM2AMD_FE_NOARM[i][0] = P_EARM2AMD_FE_NOARM[i][0];
				pEARM2AMD_FE_NOARM[i][1] = P_EARM2AMD_FE_NOARM[i][1];
				pEARM2AMD_FE_NOARM[i][2] = P_EARM2AMD_FE_NOARM[i][2] / P_EARM2AMD_FE_NOARM[i][3];
			}
		}
		else {
			// FIXME: Replace by random distributions
			// Initialize proportion of CNV (against GA) in first eye incident AMD
			for (int i = 0; i < P_CNV.length; i++) {
				pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
			}
			// Initialize first-eye incidence of EARM
			for (int i = 0; i < P_EARM.length; i++) {
				pEARM[i][0] = P_EARM[i][0];
				pEARM[i][1] = P_EARM[i][1];
				pEARM[i][2] = P_EARM[i][2] / P_EARM[i][3];
			}
			// Initialize first-eye incidence of AMD
			for (int i = 0; i < P_AMD.length; i++) {
				pAMD[i][0] = P_AMD[i][0];
				pAMD[i][1] = P_AMD[i][1];
				pAMD[i][2] = P_AMD[i][2] / P_AMD[i][3];
			}
			// Initialize probability of first-eye development of AMD from EARM
			for (int i = 0; i < P_EARM2AMD_FE_NOARM.length; i++) {
				pEARM2AMD_FE_NOARM[i][0] = P_EARM2AMD_FE_NOARM[i][0];
				pEARM2AMD_FE_NOARM[i][1] = P_EARM2AMD_FE_NOARM[i][1];
				pEARM2AMD_FE_NOARM[i][2] = P_EARM2AMD_FE_NOARM[i][2] / P_EARM2AMD_FE_NOARM[i][3];
			}
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
	public double getAMDTime(double age) {
		final double []rnd = new double[pAMD.length];
		for (int j = 0; j <pAMD.length; j++)
			rnd[j] = RNG_AMD.nextDouble();
		return getTimeToEvent(pAMD, age, rnd);
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
			double []rnd = new double[pEARM2AMD_FE_NOARM.length];
			for (int j = 0; j <pEARM2AMD_FE_NOARM.length; j++)
				rnd[j] = RNG_ARM2AMD_FE_NOARM.nextDouble();
			return getTimeToEvent(pEARM2AMD_FE_NOARM, age, rnd);
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
	
}
