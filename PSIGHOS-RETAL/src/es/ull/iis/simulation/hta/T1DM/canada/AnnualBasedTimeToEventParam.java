/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.UniqueEventParam;
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
	private final ComplicationRR rr;

	/**
	 * 
	 * @param nPatients
	 * @param annualProbability
	 * @param rr
	 */
	public AnnualBasedTimeToEventParam(int nPatients, double annualProbability, ComplicationRR rr) {
		super(nPatients);
		this.rr = rr;
		this.minusAvgTimeToEvent = -(1/annualProbability);
	}

	public Long getValue(T1DMPatient pat) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = (minusAvgTimeToEvent / rr.getRR(pat)) * Math.log(draw(pat));
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
}
