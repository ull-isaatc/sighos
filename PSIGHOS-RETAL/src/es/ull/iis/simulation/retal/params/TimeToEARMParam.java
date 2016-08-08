/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TimeToEARMParam extends SimpleEmpiricTimeToEventParam {
	// Parameters for First eye incidence of EARM --> BAD ADJUST!!!!
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

	/**
	 * @param baseCase
	 */
	public TimeToEARMParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR, P_EARM.length);
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
	public long getValidatedTimeToEvent(OphthalmologicPatient pat) {
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
