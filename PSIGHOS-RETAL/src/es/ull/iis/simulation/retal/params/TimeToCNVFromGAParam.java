/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iván Castilla Rodríguez
 * TODO Create class correctly
 */
public class TimeToCNVFromGAParam extends CompoundEmpiricTimeToEventParam {
	
	/**
	 * Minimum age. maximum age, probability of progression from GA to CNV, given NO ARM in fellow eye. 
	 * Source: Karnon model
	 */
	private final static double [][] P_CNV_E2_NOARM = { 
			{60, 70, 0.022122999},
			{70, 80, 0.042787396},
			{80, CommonParams.MAX_AGE, 0.065239556}};
	/**
	 * Minimum age. maximum age, probability of progression from GA to CNV, given EARM in both eyes. 
	 * Source: Karnon model
	 */
	private final static double [][] P_CNV_E2_EARM = { 
			{60, 70, 0.019861579},
			{70, 80, 0.039850724},
			{80, CommonParams.MAX_AGE, 0.055494935}};
	/**
	 * Minimum age. maximum age, probability of progression from GA to CNV, given GA in fellow eyes. 
	 * Source: Karnon model.
	 */
	private final static double [][] P_CNV_E2_GA = { 
			{60, 70, 0.012190125},
			{70, 80, 0.029693398},
			{80, CommonParams.MAX_AGE, 0.042591715}};

	/**
	 * Minimum age. maximum age, probability of progression from GA to CNV, given CNV in fellow eyes. 
	 * Source: Karnon model.
	 */
	private final static double [][] P_CNV_E2_CNV = { 
			{50, 70, 0.082547795},
			{70, 75, 0.111656981},
			{75, CommonParams.MAX_AGE, 0.128501303}};
	
	/**
	 * @param baseCase
	 */
	public TimeToCNVFromGAParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase, TimeUnit.YEAR);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of first-eye developing AMD from EARM
		StructuredInfo info = new StructuredInfo(P_CNV_E2_NOARM.length);
		initProbabilities(P_CNV_E2_NOARM, info.probabilities);
		tuples.put(EyeState.HEALTHY, info);
		info = new StructuredInfo(P_CNV_E2_EARM.length);
		initProbabilities(P_CNV_E2_EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		info = new StructuredInfo(P_CNV_E2_GA.length);
		initProbabilities(P_CNV_E2_GA, info.probabilities);
		tuples.put(EyeState.AMD_GA, info);
		info = new StructuredInfo(P_CNV_E2_CNV.length);
		initProbabilities(P_CNV_E2_CNV, info.probabilities);
		tuples.put(EyeState.AMD_CNV, info);
	}

	/**
	 * Returns the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 * @param pat A patient
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 */
	public long getValidatedTimeToEvent(OphthalmologicPatient pat, int eye) {
		
		final EnumSet<EyeState> otherEye = pat.getEyeState(1 - eye);
		final StructuredInfo info;
		// Other eye has CNV
		if (otherEye.contains(EyeState.AMD_CNV)) {
			 info = tuples.get(EyeState.AMD_CNV);
		}
		// Other eye has GA
		else if (otherEye.contains(EyeState.AMD_GA)) {
			info = tuples.get(EyeState.AMD_GA);
		}
		// Other eye has EARM
		else if (otherEye.contains(EyeState.EARM)) {
			info = tuples.get(EyeState.EARM);
		}
		// Other eye is healthy
		else if (otherEye.contains(EyeState.HEALTHY)) {
			info = tuples.get(EyeState.HEALTHY);
		}
		else {
			pat.error("Invalid state in other eye when computing time from EARM to AMD");
			return Long.MAX_VALUE;
		}
		return info.getValidatedTimeToEvent(pat);
	}

}
