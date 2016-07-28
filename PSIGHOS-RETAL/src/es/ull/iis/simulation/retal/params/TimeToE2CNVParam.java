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
public class TimeToE2CNVParam extends CompoundEmpiricTimeToEventParam {
	private final static double[][] P_E2CNV_E1EARM = {
			{60, 65, 0.014269706},
			{65, 70, 0.021893842},
			{70, CommonParams.MAX_AGE, 0.024646757}
	};
	
	private final static double[][] P_E2CNV_E1GA = { 
			{60, 65, 0.0167547991254577},   
			{65, 70, 0.0254060050469566},   
			{70, CommonParams.MAX_AGE, 0.0287673496030733} 
	};

	private final static double[][] P_E2CNV_E1CNV = { 
			{60, 65, 0.0492981931784431},   
			{65, 70, 0.0762051095873376},   
			{70, CommonParams.MAX_AGE, 0.0851567190055321} 
	};

	/**
	 * @param baseCase
	 */
	public TimeToE2CNVParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of fellow-eye developing CNV given EARM in first eye
		StructuredInfo info = new StructuredInfo(P_E2CNV_E1EARM.length);
		initProbabilities(P_E2CNV_E1EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		// Initialize probability of fellow-eye developing CNV given CNV in first eye
		info = new StructuredInfo(P_E2CNV_E1CNV.length);
		initProbabilities(P_E2CNV_E1CNV, info.probabilities);
		tuples.put(EyeState.AMD_CNV, info);
		// Initialize probability of fellow-eye developing CNV given GA in first eye
		info = new StructuredInfo(P_E2CNV_E1GA.length);
		initProbabilities(P_E2CNV_E1GA, info.probabilities);
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
