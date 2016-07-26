/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author Iván Castilla
 *
 */
public class TimeToAMDParam extends TimeToEventParam {
	// Parameters for first eye incidence of AMD -->  BAD ADJUST!!!!
	private final static Random RNG_AMD = new Random();
//	private final static double ALPHA_AMD = Math.exp(-11.4989645);
//	private final static double BETA_AMD = 0.05463568;
	private final static double [][] P_AMD = {	
			{55, 60, 0, 2179},
			{60, 70, 3.602758243, 12461},
			{70, 80, 6.48455774, 8314},
			{80, CommonParams.MAX_AGE, 3.019364846, 2159}};
	/** First-eye incidence of AMD */
	private final double [][] pAMD = new double[P_AMD.length][3];

	/**
	 * 
	 */
	public TimeToAMDParam(boolean baseCase) {
		super(baseCase);
		// FIXME: should work diferently when baseCase = false

		// Initialize first-eye incidence of AMD
		initProbabilities(P_AMD, pAMD);		
	}

	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		final double []rnd = new double[pAMD.length];
		for (int j = 0; j <pAMD.length; j++)
			rnd[j] = RNG_AMD.nextDouble();
		return getTimeToEvent(pAMD, age, rnd);
	}

}
