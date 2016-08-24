/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * A class to generate ages at which EARM appears. 
 * The age-adjusted probability of EARM is adapted from the Rotterdam study [1], as used in Karnon's report [2].
 * Then, the specific values are calibrated according to the SEE study in Spain [3].
 * 
 * References:
 * [1] Klaver, C.C. et al., 2001. Incidence and progression rates of age-related maculopathy: the Rotterdam Study. 
 * Investigative ophthalmology & visual science, 42(10), pp.2237�41.  
 * [2] Karnon, J. et al., 2008. A preliminary model-based assessment of the cost-utility of a screening programme for early 
 * age-related macular degeneration. Health technology assessment (Winchester, England), 12(27), pp.iii�iv, ix�124. 
 * [3] Spanish Eyes Epidemiological (SEE) Study Group, 2011. Prevalence of age-related macular degeneration in Spain. 
 * The British journal of ophthalmology, 95, pp.931�936.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TimeToEARMParam extends SimpleEmpiricTimeToEventParam {
	// Parameters for First eye incidence of EARM --> BAD ADJUST!!!!
//	private final static double ALPHA_EARM = Math.exp(-11.41320441);
//	private final static double BETA_EARM = 0.097865047;
	/** Calibration parameter for early ages to adjust the simulated prevalence to that observed in [3] */ 
	private final static double CALIBRATION_FACTOR_1 = ARMDParams.CALIBRATED ? 3.0 : 1.0;;
	/** Calibration parameters for late ages to adjust the simulated prevalence to that observed in [3] */ 
	private final static double CALIBRATION_FACTOR_2 = ARMDParams.CALIBRATED ? 1.0 : 1.0;
	/** Parameters for an empirical distribution on incidence of EARM. Source: [3] */
	private final static double [][] P_EARM = {	
			{55, 60, 3 * CALIBRATION_FACTOR_1, 2179},
			{60, 65, 32 * CALIBRATION_FACTOR_1, 6085},
			{65, 70, 69 * CALIBRATION_FACTOR_1, 6376},
			{70, 75, 97 * CALIBRATION_FACTOR_2, 5102},
			{75, 80, 102 * CALIBRATION_FACTOR_2, 3212},
			{80, CommonParams.MAX_AGE, 110 * CALIBRATION_FACTOR_2, 2159}};

	/**
	 * @param baseCase
	 */
	public TimeToEARMParam(RETALSimulation simul, boolean baseCase) {
		super(simul, baseCase, TimeUnit.YEAR, P_EARM.length);
		// FIXME: should work differently when baseCase = false
		
		// Initialize first-eye incidence of EARM
		initProbabilities(P_EARM, probabilities);		
	}

	/**
	/**
	 * Generates a valid time to EARM, i.e., an event time that is either lower than the time to death or INFINITE 
	 * @param age Current age of the patient
	 * @param timeToDeath The expected time to death of the patient
	 * @return A time to EARM that is lower than time to death or INFINITE
	 */
	public long getValidatedTimeToEvent(Patient pat) {
		long timeToEARM;
		final long timeToDeath = pat.getTimeToDeath();
		final long currentTime = pat.getTs();
		
		// If there are no stored values in the queue, generate a new one
		if (queue.isEmpty()) {
			timeToEARM = getTimeToEvent(pat);
		}
		// If there are stored values in the queue, I try with them in the first place
		else {
			final Iterator<Long> iter = queue.iterator();
			do {
				timeToEARM = iter.next();
				if (timeToEARM < timeToDeath)
					iter.remove();
				// Check if the stored time already passed --> If so, discharge
				if (timeToEARM <= currentTime)
					timeToEARM = Long.MAX_VALUE;
			} while (iter.hasNext() && timeToEARM >= timeToDeath);
			// If no valid event is found, generate a new one
			if (timeToEARM >= timeToDeath)
				timeToEARM = getTimeToEvent(pat);
		}
		// Generate new times to event until we get a valid one
		while (timeToEARM != Long.MAX_VALUE && timeToEARM >= timeToDeath) {
			queue.push(timeToEARM);
			timeToEARM = getTimeToEvent(pat);
		}
		return timeToEARM;
	}
	
}
