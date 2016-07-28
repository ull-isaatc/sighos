/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.OphthalmologicPatient;

/**
 * @author Iván Castilla
 *
 */
public class TimeToAMDParam extends SimpleEmpiricTimeToEventParam {
	// Parameters for first eye incidence of AMD -->  BAD ADJUST!!!!
//	private final static double ALPHA_AMD = Math.exp(-11.4989645);
//	private final static double BETA_AMD = 0.05463568;
	private final static double [][] P_AMD = {	
			{55, 60, 0, 2179},
			{60, 70, 3.602758243, 12461},
			{70, 80, 6.48455774, 8314},
			{80, CommonParams.MAX_AGE, 3.019364846, 2159}};
	
	/**
	 * 
	 */
	public TimeToAMDParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR, P_AMD.length);
		// FIXME: should work diferently when baseCase = false

		// Initialize first-eye incidence of AMD
		initProbabilities(P_AMD, probabilities);		
	}

	@Override
	public long getValidatedTimeToEvent(OphthalmologicPatient pat, boolean firstEye) {
		long timeToAMD;
		final long timeToEARM = pat.getTimeToEARM();
		final long timeToDeath = pat.getTimeToDeath();		
		
		// If we obtained a valid time to EARM, we don't need time to AMD. However, if we don't use the "time to AMD" generator, we would 
		// be artificially underestimating the incidence of AMD in healthy eyes. Hence, we have to use the random distribution to create a 
		// valid time to AMD
		if (timeToEARM < timeToDeath) {
			// First, we calibrate the time to AMD distribution until we get a valid time (in this case, INFINITE is a valid case) 
			timeToAMD = getTimeToEvent(pat, firstEye);
			// Generate new times to event until we get a valid one
			while (timeToAMD != Long.MAX_VALUE) {
				queue.push(timeToAMD);
				timeToAMD = getTimeToEvent(pat, firstEye);
			}
		}
		else {
			// If there are no stored values in the queue, generate a new one
			if (queue.isEmpty()) {
				timeToAMD = getTimeToEvent(pat, firstEye);
			}
			// If there are stored values in the queue, I try with them in the first place
			else {
				final Iterator<Long> iter = queue.iterator();
				do {
					timeToAMD = iter.next();
					if (timeToAMD < timeToDeath)
						iter.remove();
				} while (iter.hasNext() && timeToAMD >= timeToDeath);
				// If no valid event is found, generate a new one
				if (timeToAMD >= timeToDeath)
					timeToAMD = getTimeToEvent(pat, firstEye);
			}
			// Generate new times to event until we get a valid one
			while (timeToAMD != Long.MAX_VALUE && timeToAMD >= timeToDeath) {
				queue.push(timeToAMD);
				timeToAMD = getTimeToEvent(pat, firstEye);
			}
		}			
		return timeToAMD;
	}

}
