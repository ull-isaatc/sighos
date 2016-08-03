/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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

	/** Minimum age. maximum age, CNV cases, and total cases of incident AMD */
	private final static double [][] P_CNV = {
			{60, 65, 1, 1},
			{65, 70, 2, 5},
			{70, 75, 7, 10},
			{75, 80, 9, 14},
			{80, CommonParams.MAX_AGE, 9, 17}};
	
	/** Proportion of CNV (against GA) in first eye incident AMD */
	private final TreeMap<Integer, Double> pCNV = new TreeMap<Integer, Double>();
	
	
	/**
	 * 
	 */
	public TimeToAMDParam(boolean baseCase) {
		super(baseCase, TimeUnit.YEAR, P_AMD.length);
		// FIXME: should work diferently when baseCase = false

		// Initialize first-eye incidence of AMD
		initProbabilities(P_AMD, probabilities);		
		// Initialize proportion of CNV (against GA) in first eye incident AMD
		for (int i = 0; i < P_CNV.length; i++) {
			pCNV.put((int)P_CNV[i][0], P_CNV[i][2]/ P_CNV[i][3]);
		}
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
			// First, we calibrate the time to AMD distribution until we get a valid time (in this case, INFINITE is a valid value) 
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

	public boolean isCNV(OphthalmologicPatient pat, boolean firstEye) {
		final Map.Entry<Integer, Double> entry = pCNV.lowerEntry((int)pat.getAge());
		if (entry != null) {
			final double rnd = (firstEye) ? pat.getRndProbCNV1() : pat.getRndProbCNV2();
			return (rnd <= entry.getValue());
		}
		return false;		
	}
}
