/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumSet;
import java.util.Iterator;
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
	private final static double [][] P_EARM2AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317, 359},
			{70, 80, 16.6795708705063, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988, 335}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given EARM in both eyes. 
	 * Source: Karnon model
	 */
	private final static double [][] P_EARM2AMD_E2_EARM = { 
			{60, 70, 4.070393438, 84.40999139},
			{70, 80, 9.631457912, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696, 13.78122308}};
	/**
	 * Minimum age. maximum age, incident patients and patients-year at risk that progress from EARM to AMD, given GA in fellow eyes. 
	 * Source: Karnon model (assumed to be equal to progression rate from EARM to AMD, given EARM in fellow eye.
	 */
	private final static double [][] P_EARM2AMD_E2_GA = { 
			{60, 70, 4.070393438, 84.40999139},
			{70, 80, 9.631457912, 87.85529716},
			{80, CommonParams.MAX_AGE, 2.298334696, 13.78122308}};

	/** Minimum age. maximum age, CNV cases, and total cases of AMD evolving from EARM */
	private final static double [][] P_CNV = {
			{60, 70, 3, 6},
			{70, 80, 16, 24},
			{80, CommonParams.MAX_AGE, 9, 17}};

	/** Proportion of CNV (against GA) in eye evolving from EARM */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	
	/**
	 * @param baseCase
	 */
	public TimeToAMDFromEARMParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR);
		// FIXME: should work diferently when baseCase = false
		
		// Initialize probability of first-eye developing AMD from EARM
		StructuredInfo info = new StructuredInfo(P_EARM2AMD_E2_NOARM.length);
		initProbabilities(P_EARM2AMD_E2_NOARM, info.probabilities);
		tuples.put(EyeState.HEALTHY, info);
		info = new StructuredInfo(P_EARM2AMD_E2_EARM.length);
		initProbabilities(P_EARM2AMD_E2_EARM, info.probabilities);
		tuples.put(EyeState.EARM, info);
		info = new StructuredInfo(P_EARM2AMD_E2_GA.length);
		initProbabilities(P_EARM2AMD_E2_GA, info.probabilities);
		tuples.put(EyeState.AMD_GA, info);
		// Initialize proportion of CNV (against GA) in eye evolving from EARM
		for (int i = 0; i < P_CNV.length; i++) {
			pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
		}
	}

	@Override
	// TODO: Fix completely
	public long getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {
		long timeToAMD;
		final long timeToEARM = pat.getTimeToEARM();
		final long timeToDeath = pat.getTimeToDeath();
		
		if (timeToEARM >= timeToDeath) {
			pat.error("Invalid time to EARM when computing time from EARM to AMD");
			timeToAMD = Long.MAX_VALUE;
		}
		else {
			final EnumSet<EyeState> otherEye = (firstEye) ? pat.getEye2State() : pat.getEye1State();
			final StructuredInfo info;
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
			else if (otherEye.contains(EyeState.HEALTHY)) {
				info = tuples.get(EyeState.HEALTHY);
			}
			else {
				pat.error("Invalid state in other eye when computing time from EARM to AMD");
				return Long.MAX_VALUE;
			}
			
			// If there are no stored values in the queue, generate a new one
			if (info.queue.isEmpty()) {
				timeToAMD = info.getTimeToEvent(pat);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Long> iter = info.queue.iterator();
				do {
					timeToAMD = iter.next();
					if (timeToAMD < timeToDeath)
						iter.remove();
				} while (iter.hasNext() && timeToAMD >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToAMD >= timeToDeath)
					timeToAMD = info.getTimeToEvent(pat);
			}
			// Generate new times to event until we get a valid one
			while (timeToAMD != Long.MAX_VALUE && timeToAMD >= timeToDeath) {
				info.queue.push(timeToAMD);
				timeToAMD = info.getTimeToEvent(pat);
			}
		}			
		return timeToAMD;
	}

	public boolean isCNV(OphthalmologicPatient pat, boolean firstEye) {
		final Map.Entry<Integer, Double> entry = pCNV.lowerEntry((int)pat.getAge());
		if (entry != null) {
			final double rnd = (firstEye) ? pat.getRndProbCNV1() : pat.getRndProbCNV2();
			return (rnd <= entry.getValue());
		}
		return false;		
	}
}
