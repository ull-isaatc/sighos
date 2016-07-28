/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;

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
	private final static double [][] P_EARM2AMD_E2_NOARM = { 
			{60, 70, 3.63796991807317, 359},
			{70, 80, 16.6795708705063, 724},
			{80, CommonParams.MAX_AGE, 11.7406621342988, 335}};
	
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
