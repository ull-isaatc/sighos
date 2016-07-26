/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author masbe_000
 *
 */
public class TimeToAMDFromEARMParam extends TimeToEventParam {
	// Parameters for progression to AMD given that FE has no ARM --> Adjust not tested
	private final static Random RNG_ARM2AMD_E2_NOARM = new Random();
//	private final static double ALPHA_ARM2AMD_FE_NOARM = Math.exp(-7.218078288);
//	private final static double BETA_ARM2AMD_FE_NOARM = 0.043182526;
	private final static double [][] P_EARM2AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317, 359},
			{70, 80, 16.6795708705063, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988, 335}};
	
	/** Probability of developing AMD in first eye with EARM, given the state of the fellow eye */ 
	private final EnumMap<EyeState, double[][]> pEARM2AMD = new EnumMap<EyeState, double[][]>(EyeState.class);

	/**
	 * @param baseCase
	 */
	public TimeToAMDFromEARMParam(boolean baseCase) {
		super(baseCase);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of first-eye developing AMD from EARM
		pEARM2AMD.put(EyeState.HEALTHY, new double[P_EARM2AMD_E2_NOARM.length][3]);
		initProbabilities(P_EARM2AMD_E2_NOARM, pEARM2AMD.get(EyeState.HEALTHY));
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.TimeToEventParam#getTimeToEvent(double, java.util.EnumSet, java.util.EnumSet)
	 */
	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
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

}
