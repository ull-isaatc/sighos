/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeToAMDFromEARMParam extends CompoundEmpiricTimeToEventParam {
	// Parameters for progression to AMD given that FE has no ARM --> Adjust not tested
//	private final static double ALPHA_ARM2AMD_FE_NOARM = Math.exp(-7.218078288);
//	private final static double BETA_ARM2AMD_FE_NOARM = 0.043182526;
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given NO ARM in fellow eye. 
	 * Source: Karnon model
	 */
	private final static double [][] P_AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317, 359},
			{70, 80, 16.6795708705063, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988, 335}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given EARM in both eyes. 
	 * Source: Karnon model
	 */
	private final static double [][] P_AMD_E2_EARM = { 
			{60, 70, 4.070393438, 84.40999139},
			{70, 80, 9.631457912, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696, 13.78122308}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given GA in fellow eyes. 
	 * Source: Karnon model (assumed to be equal to progression rate from EARM to AMD, given EARM in fellow eye).
	 */
	private final static double [][] P_AMD_E2_GA = { 
			{60, 70, 4.070393438, 84.40999139},
			{70, 80, 9.631457912, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696, 13.78122308}};

	/**
	 * Minimum age. maximum age, probability of progression from EARM to AMD, given CNV in fellow eyes. 
	 * Source: Karnon model (I have added both CNV and GA probabilities and then I apply.
	 */
	private final static double [][] P_AMD_E2_CNV = { 
			{50, 70, 0.042279983+0.082645813},
			{70, 75, 0.05732427+0.111660068},
			{75, CommonParams.MAX_AGE, 0.066058103+0.128941355}};
	
	/** Minimum age. maximum age, CNV cases, and total cases of AMD evolving from EARM */
	private final static double [][] P_CNV = {
			{60, 70, 3, 6},
			{70, 80, 16, 24},
			{80, CommonParams.MAX_AGE, 9, 17}};

	/** Minimum age. maximum age, probability CNV, and probability of CNV+GA evolving from EARM, when fellow eye has CNV */
	private final static double [][] P_CNV_E2_CNV = {
			{50, 70, 0.082645813, 0.042279983+0.082645813},
			{70, 75, 0.111660068, 0.05732427+0.111660068},
			{75, CommonParams.MAX_AGE, 0.128941355, 0.066058103+0.128941355}};

	/** Proportion of CNV (against GA) in eye evolving from EARM */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	/** Proportion of CNV (against GA) in eye evolving from EARM when fellow eye has CNV*/
	private final TreeMap<Integer, Double> pCNV_E2_CNV = new TreeMap<Integer, Double>();
	
	/**
	 * @param baseCase
	 */
	public TimeToAMDFromEARMParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of first-eye developing AMD from EARM
		StructuredInfo info = new StructuredInfo(P_AMD_E2_NOARM.length);
		initProbabilities(P_AMD_E2_NOARM, info.probabilities);
		tuples.put(EyeState.HEALTHY, info);
		info = new StructuredInfo(P_AMD_E2_EARM.length);
		initProbabilities(P_AMD_E2_EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		info = new StructuredInfo(P_AMD_E2_GA.length);
		initProbabilities(P_AMD_E2_GA, info.probabilities);
		tuples.put(EyeState.AMD_GA, info);
		info = new StructuredInfo(P_AMD_E2_CNV.length);
		initProbabilities(P_AMD_E2_CNV, info.probabilities);
		tuples.put(EyeState.AMD_CNV, info);
		
		// Initialize proportion of CNV (against GA) in eye evolving from EARM 
		for (int i = 0; i < P_CNV.length; i++) {
			pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
		}
		// Initialize proportion of CNV (against GA) in eye evolving from EARM when fellow eye has CNV
		for (int i = 0; i < P_CNV_E2_CNV.length; i++) {
			pCNV_E2_CNV.put((int)P_CNV_E2_CNV[i][0], P_CNV_E2_CNV[i][2]/ P_CNV_E2_CNV[i][3]);
		}
	}

	/**
	 * Returns the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 * @param pat A patient
	 * @param firstEye True if the event applies to the first eye; false if the event applies to the fellow eye
	 * @return the simulation time when a specific event will happen (expressed in simulation time units), and adjusted so 
	 * the time is coherent with the state and future/past events of the patient
	 */
	public long[] getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {		
		final EnumSet<EyeState> otherEye = (firstEye) ? pat.getEye2State() : pat.getEye1State();
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
			return null;
		}
		
		final long timeToAMD = info.getValidatedTimeToEvent(pat);
		
		// If no event, no need to choose type of AMD
		if (timeToAMD == Long.MAX_VALUE)
			return null;
		
		// Choose CNV or GA
		final Map.Entry<Integer, Double> entry = (otherEye.contains(EyeState.AMD_CNV)) ? 
				pCNV_E2_CNV.lowerEntry((int)pat.getAge()) : pCNV.lowerEntry((int)pat.getAge());
		// TODO: Check if this condition should arise an error				
		if (entry == null) {
			return new long[] {timeToAMD, EyeState.AMD_GA.ordinal()};
		}
		final double rnd = (firstEye) ? pat.getRndProbCNV1() : pat.getRndProbCNV2();
		return new long[] {timeToAMD, (rnd <= entry.getValue()) ? EyeState.AMD_CNV.ordinal() : EyeState.AMD_GA.ordinal()};
	}

}
