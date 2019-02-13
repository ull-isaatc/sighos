/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A complication submodel
 * @author Iván Castilla Rodríguez
 */
public abstract class ComplicationSubmodel {
	/** A flag to enable or disable this complication during the simulation run */
	protected boolean enable;
	
	/**
	 * Creates a new complication submodel 
	 */
	public ComplicationSubmodel() {
		enable = true;
	}

	/**
	 * Disables this complication
	 */
	public void disable() {
		enable = false;
	}
	
	/**
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. The time
	 * generated cannot exceed a limit (generally, death time or a previously computed time to event).  
	 * @param pat A patient
	 * @param minusAvgTimeToEvent -1/(annual risk of the event)
	 * @param rnd A random number
	 * @param rr Relative risk for the patient
	 * @param limit The maximum timestamp when this event may happen
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(T1DMPatient pat, double minusAvgTimeToEvent, double rnd, double rr, long limit) {
		// In case the probability of transition was 0
		if (Double.isInfinite(minusAvgTimeToEvent))
			return Long.MAX_VALUE;
		final double newMinus = -1 / (1-Math.exp(Math.log(1+1/minusAvgTimeToEvent)*rr));
		final double time = (newMinus) * Math.log(rnd);
		final long absTime = pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		return (absTime >= limit) ? Long.MAX_VALUE : absTime;
	}
	
	/**
	 * Returns the minimum among a set of values
	 * @param limit Reference value
	 * @param args Rest of values to compare
	 * @return the minimum among a set of values
	 */
	public static long min(long limit, long... args) {
		for (long value : args) {
			if (value < limit)
				limit = value;
		}
		return limit;
	}
}
