/**
 * 
 */
package es.ull.iis.simulation.hta.retal.params;

import java.util.Iterator;
import java.util.LinkedList;

import es.ull.iis.simulation.hta.retal.RetalPatient;
import es.ull.iis.simulation.hta.retal.RandomForPatient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A class to generate ages at which EARM appears. 
 * The age-adjusted probability of EARM is adapted from the Rotterdam study [1], as used in Karnon's report [2].
 * Then, the specific values are calibrated according to the SEE study in Spain [3].
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
public class TimeToEARMParam extends EmpiricTimeToEventParam {
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
	/** An internal list of generated times to event to be used when creating validated times to event */
	final protected LinkedList<Long> queue = new LinkedList<Long>();
	/** First-eye incidence of EARM */
	final protected double [][] probabilities;	

	/**
	 * @param 
	 */
	public TimeToEARMParam() {
		super(TimeUnit.YEAR);
		this.probabilities = new double[P_EARM.length][3];
		// TODO: should work differently when  = false
		
		// Initialize first-eye incidence of EARM
		initProbabilities(P_EARM, probabilities);		
	}
	
	/**
	 * Returns the "brute" simulation time when a specific event will happen (expressed in simulation time units)
	 * @param pat A patient
	 * @return the simulation time when a specific event will happen (expressed in simulation time units)
	 */
	public long getTimeToEvent(RetalPatient pat) {
		final double []rnd = pat.draw(RandomForPatient.ITEM.TIME_TO_EARM, probabilities.length);
		final double time = getTimeToEvent(probabilities, pat.getAge(), rnd);
		return (time == Double.MAX_VALUE) ? Long.MAX_VALUE : pat.getTs() + Math.max(CommonParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, unit));
	}

	/**
	 * Generates a valid time to EARM, i.e., an event time that is either lower than the time to death or INFINITE 
	 * @param age Current age of the patient
	 * @param timeToDeath The expected time to death of the patient
	 * @return A time to EARM that is lower than time to death or INFINITE
	 */
	public long getValidatedTimeToEvent(RetalPatient pat) {
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
