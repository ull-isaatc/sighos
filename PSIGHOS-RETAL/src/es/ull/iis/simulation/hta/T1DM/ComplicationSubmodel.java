/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class ComplicationSubmodel {
	protected boolean enable;
	/**
	 * 
	 */
	public ComplicationSubmodel() {
		enable = true;
	}

	public abstract T1DMProgression getNextComplication(T1DMPatient pat);
	public abstract int getNSubstates();
	public abstract T1DMComorbidity[] getSubstates();
	public abstract TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat);
	public abstract double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge);
	public abstract double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent);
	public abstract double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method);
	
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
		final double time = (minusAvgTimeToEvent / rr) * Math.log(rnd);
		final long absTime = pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
		return (absTime >= limit) ? Long.MAX_VALUE : absTime;
	}

	public static long min(long limit, long... args) {
		for (long value : args) {
			if (value < limit)
				limit = value;
		}
		return limit;
	}
}