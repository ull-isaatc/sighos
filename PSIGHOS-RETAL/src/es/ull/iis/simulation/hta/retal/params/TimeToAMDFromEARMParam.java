/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.retal.EyeState;
import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A class to generate time to AMD in an eye with EARM. 
 * The age-adjusted probability of AMD is adapted from the Rotterdam study [1], as used in Karnon's report [2].
 * 
 * References:
 * [1] Klaver, C.C. et al., 2001. Incidence and progression rates of age-related maculopathy: the Rotterdam Study. 
 * Investigative ophthalmology & visual science, 42(10), pp.2237–41.  
 * [2] Karnon, J. et al., 2008. A preliminary model-based assessment of the cost-utility of a screening programme for early 
 * age-related macular degeneration. Health technology assessment (Winchester, England), 12(27), pp.iii–iv, ix–124. 
 * [3] Spanish Eyes Epidemiological (SEE) Study Group, 2011. Prevalence of age-related macular degeneration in Spain. 
 * The British journal of ophthalmology, 95, pp.931–936.
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeToAMDFromEARMParam extends CompoundEmpiricTimeToEventParam {
	// Parameters for progression to AMD given that FE has no ARM --> Adjust not tested
//	private final static double ALPHA_ARM2AMD_FE_NOARM = Math.exp(-7.218078288);
//	private final static double BETA_ARM2AMD_FE_NOARM = 0.043182526;
	/** Calibration parameter to adjust the simulated prevalence to that observed in [3] */ 
	private final static double CALIBRATION_FACTOR = ARMDParams.CALIBRATED ? 5.0 : 1.0;
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given NO ARM in fellow eye. 
	 * Source: [2]
	 */
	private final static double [][] P_AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317 * CALIBRATION_FACTOR, 359},
			{70, 80, 16.6795708705063 * CALIBRATION_FACTOR, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988 * CALIBRATION_FACTOR, 335}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given EARM in both eyes. 
	 * Source: [2]
	 */
	private final static double [][] P_AMD_E2_EARM = { 
			{60, 70, 4.070393438 * CALIBRATION_FACTOR, 84.40999139},
			{70, 80, 9.631457912 * CALIBRATION_FACTOR, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696 * CALIBRATION_FACTOR, 13.78122308}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given GA in fellow eyes. 
	 * Source: [2] (assumed to be equal to progression rate from EARM to AMD, given EARM in fellow eye).
	 */
	private final static double [][] P_AMD_E2_GA = { 
			{60, 70, 4.070393438 * CALIBRATION_FACTOR, 84.40999139},
			{70, 80, 9.631457912 * CALIBRATION_FACTOR, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696 * CALIBRATION_FACTOR, 13.78122308}};

	/**
	 * Minimum age. maximum age, probability of progression from EARM to AMD, given CNV in fellow eyes. 
	 * Source: [2] (I have added both CNV and GA probabilities and then I apply.
	 */
	private final static double [][] P_AMD_E2_CNV = { 
			{50, 70, (0.042279983+0.082645813) * CALIBRATION_FACTOR},
			{70, 75, (0.05732427+0.111660068) * CALIBRATION_FACTOR},
			{75, CommonParams.MAX_AGE, (0.066058103+0.128941355) * CALIBRATION_FACTOR}};
	
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
	 * @param secondOrder
	 */
	public TimeToAMDFromEARMParam() {
		super(TimeUnit.YEAR);
		// TODO: should work diferently when secondOrder = false
		
		// Initialize probability of first-eye developing AMD from EARM
		StructuredInfo info = new StructuredInfo(P_AMD_E2_NOARM.length, RandomForPatient.ITEM.TIME_TO_AMD_E2_NOARM);
		initProbabilities(P_AMD_E2_NOARM, info.probabilities);
		tuples.put(EyeState.HEALTHY, info);
		info = new StructuredInfo(P_AMD_E2_EARM.length, RandomForPatient.ITEM.TIME_TO_AMD_E2_EARM);
		initProbabilities(P_AMD_E2_EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		info = new StructuredInfo(P_AMD_E2_GA.length, RandomForPatient.ITEM.TIME_TO_AMD_E2_GA);
		initProbabilities(P_AMD_E2_GA, info.probabilities);
		tuples.put(EyeState.AMD_GA, info);
		info = new StructuredInfo(P_AMD_E2_CNV.length, RandomForPatient.ITEM.TIME_TO_AMD_E2_CNV);
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
	public EyeStateAndValue getValidatedTimeToEventAndState(RetalPatient pat, int eye) {		
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
		// Other eye is healthy or affected by a different disease
		else {
			info = tuples.get(EyeState.HEALTHY);
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
			return new EyeStateAndValue(EyeState.AMD_GA, timeToAMD);
		}
		final double rnd = (eye == 0) ? pat.draw(RandomForPatient.ITEM.ARMD_P_CNV1) : pat.draw(RandomForPatient.ITEM.ARMD_P_CNV2);
		return new EyeStateAndValue((rnd <= entry.getValue()) ? EyeState.AMD_CNV : EyeState.AMD_GA, timeToAMD);
	}

}
