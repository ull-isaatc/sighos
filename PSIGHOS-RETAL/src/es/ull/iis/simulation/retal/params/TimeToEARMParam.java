/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.Random;

import es.ull.iis.simulation.retal.EyeState;

/**
 * @author masbe_000
 *
 */
public class TimeToEARMParam extends TimeToEventParam {
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
	/** First-eye incidence of EARM */
	private final double [][] pEARM = new double[P_EARM.length][3];	

	/**
	 * @param baseCase
	 */
	public TimeToEARMParam(boolean baseCase) {
		super(baseCase);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize first-eye incidence of EARM
		initProbabilities(P_EARM, pEARM);		
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.retal.params.TimeToEventParam#getTimeToEvent(double, java.util.EnumSet, java.util.EnumSet)
	 */
	@Override
	public double getTimeToEvent(double age, EnumSet<EyeState> firstEye, EnumSet<EyeState> fellowEye) {
		final double []rnd = new double[pEARM.length];
		for (int j = 0; j <pEARM.length; j++)
			rnd[j] = RNG_EARM.nextDouble();
		return getTimeToEvent(pEARM, age, rnd);
	}

}
