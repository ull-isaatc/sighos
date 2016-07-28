/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla
 *
 */
public class TimeToE2GAParam extends CompoundEmpiricTimeToEventParam {
	private final static double[][] P_E2GA_E1EARM = {
			{60, 65, 0.049910436},
			{65, 70, 0.060853865},
			{70, CommonParams.MAX_AGE, 0.079085662}
	};
	
	private final static double[][] P_E2GA_E1GA = { 
			{60, 65, 0.0538734777861744},   
			{65, 70, 0.0667204941316263},   
			{70, CommonParams.MAX_AGE, 0.0850931858956147} 
	};

	private final static double[][] P_E2GA_E1CNV = { 
			{60, 65, 0.00591561395661435},   
			{65, 70, 0.00706997998864217},   
			{70, CommonParams.MAX_AGE, 0.00925261820611631} 
	};


	/**
	 * @param baseCase
	 */
	public TimeToE2GAParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of fellow-eye developing GA given EARM in first eye
		StructuredInfo info = new StructuredInfo(P_E2GA_E1EARM.length);
		initProbabilities(P_E2GA_E1EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		// Initialize probability of fellow-eye developing GA given CNV in first eye
		info = new StructuredInfo(P_E2GA_E1CNV.length);
		initProbabilities(P_E2GA_E1CNV, info.probabilities);
		tuples.put(EyeState.AMD_CNV, info);
		// Initialize probability of fellow-eye developing GA given GA in first eye
		info = new StructuredInfo(P_E2GA_E1GA.length);
		initProbabilities(P_E2GA_E1GA, info.probabilities);
		tuples.put(EyeState.AMD_GA, info);

		// FIXME: Check
		tuples.put(EyeState.HEALTHY, new StructuredInfo());
	}


	@Override
	public long getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {
		// TODO Auto-generated method stub
		return 0;
	}

}
