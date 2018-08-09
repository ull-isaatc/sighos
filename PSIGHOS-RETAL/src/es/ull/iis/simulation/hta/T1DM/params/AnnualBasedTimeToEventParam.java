/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A parameter to compute time to event, which is based on an annual transition probability. Time to event is computed by 
 * transforming the annual transition probability to the equivalent probability thorough the patient lifetime. Different RRs can be
 * applied to each intervention.
 * @author Ivan Castilla Rodriguez
 *
 */
public class AnnualBasedTimeToEventParam extends UniqueEventParam<Long> {
	private final double minusAvgTimeToEvent;
	private final double[] interventionRR;

	/**
	 * 
	 * @param nPatients
	 * @param annualProbability
	 * @param interventionRR
	 */
	public AnnualBasedTimeToEventParam(int nPatients, double annualProbability, double[] interventionRR) {
		super(nPatients);
		this.interventionRR = interventionRR;
		this.minusAvgTimeToEvent = -(1/annualProbability);
	}

	public Long getValue(T1DMPatient pat) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = (minusAvgTimeToEvent / interventionRR[pat.getnIntervention()]) * Math.log(draw(pat));
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(CommonParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
}
