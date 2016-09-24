/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.RandomForPatient;

/**
 * A parameter to compute time to event, which is based on an annual transition probability. Time to event is computed by 
 * transforming the annual transition probability to the equivalent probability thorough the patient lifetime.
 * @author Ivan Castilla Rodriguez
 *
 */
public class AnnualBasedTimeToEventParam extends Param {
	final private double minusAvgTimeToEvent;
	final private RandomForPatient.ITEM item;
	/**
	 * @param simul
	 * @param baseCase
	 */
	public AnnualBasedTimeToEventParam(boolean baseCase, double annualProbability, RandomForPatient.ITEM item) {
		super(baseCase);
		this.item = item;
		this.minusAvgTimeToEvent = -(1/annualProbability);
	}

	public long getTimeToEvent(Patient pat) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = minusAvgTimeToEvent * Math.log(pat.draw(item));
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(CommonParams.MIN_TIME_TO_EVENT,pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
}
