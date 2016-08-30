/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * A class to generate time to first eye incidence of AMD. Either CNV or GA can appear.
 * The age-adjusted probability of AMD is adapted from the Rotterdam study [1], as used in Karnon's report [2].
 * 
 * References:
 * [1] Klaver, C.C. et al., 2001. Incidence and progression rates of age-related maculopathy: the Rotterdam Study. 
 * Investigative ophthalmology & visual science, 42(10), pp.2237–41.  
 * [2] Karnon, J. et al., 2008. A preliminary model-based assessment of the cost-utility of a screening programme for early 
 * age-related macular degeneration. Health technology assessment (Winchester, England), 12(27), pp.iii–iv, ix–124. 
 * [3] Spanish Eyes Epidemiological (SEE) Study Group, 2011. Prevalence of age-related macular degeneration in Spain. 
 * The British journal of ophthalmology, 95, pp.931–936.
 * @author Iván Castilla
 *
 */
public class TimeToE1AMDParam extends EmpiricTimeToEventParam {
	// Parameters for first eye incidence of AMD -->  BAD ADJUST!!!!
//	private final static double ALPHA_AMD = Math.exp(-11.4989645);
//	private final static double BETA_AMD = 0.05463568;
	/** Calibration parameter to adjust the simulated prevalence to that observed in [3] */ 
	private final static double CALIBRATION_FACTOR_1 = ARMDParams.CALIBRATED ? 3.0 : 1.0;
	private final static double [][] P_AMD = {	
			{55, 60, 0 * CALIBRATION_FACTOR_1, 2179},
			{60, 70, 3.602758243 * CALIBRATION_FACTOR_1, 12461},
			{70, 80, 6.48455774 * CALIBRATION_FACTOR_1, 8314},
			{80, CommonParams.MAX_AGE, 3.019364846 * CALIBRATION_FACTOR_1, 2159}};

	/** Minimum age. maximum age, CNV cases, and total cases of incident AMD */
	private final static double [][] P_CNV = {
			{60, 65, 1, 1},
			{65, 70, 2, 5},
			{70, 75, 7, 10},
			{75, 80, 9, 14},
			{80, CommonParams.MAX_AGE, 9, 17}};
	
	/** Proportion of CNV (against GA) in first eye incident AMD */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	/** An internal list of generated times to event to be used when creating validated times to event */
	final protected LinkedList<Long> queue = new LinkedList<Long>();
	/** First-eye incidence of EARM */
	final protected double [][] probabilities;	
	
	
	/**
	 * 
	 */
	public TimeToE1AMDParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase, TimeUnit.YEAR);
		this.probabilities = new double[P_AMD.length][3];
		// FIXME: should work diferently when baseCase = false

		// Initialize first-eye incidence of AMD
		initProbabilities(P_AMD, probabilities);		
		// Initialize proportion of CNV (against GA) in first eye incident AMD
		for (int i = 0; i < P_CNV.length; i++) {
			pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
		}
	}

	/**
	 * Returns the "brute" simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(Patient pat) {
		final double []rnd = pat.getRandomNumber(RandomForPatient.ITEM.TIME_TO_E1AMD, probabilities.length);
		final double time = getTimeToEvent(probabilities, pat.getAge(), rnd);
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + simul.getTimeUnit().convert(time, unit);
	}

	public EyeStateAndValue getValidatedTimeToEventAndState(Patient pat) {
		long timeToAMD;
		
		final long timeToEARM = pat.getTimeToEyeState(EyeState.EARM, 0);
		final long timeToDeath = pat.getTimeToDeath();		
		final long currentTime = pat.getTs();
		
		// If we obtained a valid time to EARM, we don't need time to AMD. However, if we don't use the "time to AMD" generator, we would 
		// be artificially underestimating the incidence of AMD in healthy eyes. Hence, we have to use the random distribution to create a 
		// valid time to AMD
		if (timeToEARM < timeToDeath) {
			// First, we calibrate the time to AMD distribution until we get a valid time (in this case, INFINITE is a valid value) 
			timeToAMD = getTimeToEvent(pat);
			// Generate new times to event until we get a valid one
			while (timeToAMD != Long.MAX_VALUE) {
				queue.push(timeToAMD);
				timeToAMD = getTimeToEvent(pat);
			}
		}
		else {
			// If there are no stored values in the queue, generate a new one
			if (queue.isEmpty()) {
				timeToAMD = getTimeToEvent(pat);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Long> iter = queue.iterator();
				do {
					timeToAMD = iter.next();
					if (timeToAMD < timeToDeath)
						iter.remove();
					// Check if the stored time already passed --> If so, discharge
					if (timeToAMD <= currentTime)
						timeToAMD = Long.MAX_VALUE;
				} while (iter.hasNext() && timeToAMD >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToAMD >= timeToDeath)
					timeToAMD = getTimeToEvent(pat);
			}
			// Generate new times to event until we get a valid one
			while (timeToAMD != Long.MAX_VALUE && timeToAMD >= timeToDeath) {
				queue.push(timeToAMD);
				timeToAMD = getTimeToEvent(pat);
			}
		}
		if (timeToAMD == Long.MAX_VALUE)
			return null;
		
		final Map.Entry<Integer, Double> entry = pCNV.lowerEntry((int)pat.getAge());
		// TODO: Check if this condition should arise an error
		if (entry == null) {
			return new EyeStateAndValue(EyeState.AMD_GA, timeToAMD);
		}
		final double rnd = pat.getRandomNumber(RandomForPatient.ITEM.ARMD_P_CNV1);
		return new EyeStateAndValue((rnd <= entry.getValue()) ? EyeState.AMD_CNV : EyeState.AMD_GA, timeToAMD);
	}
}
