/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * @author Iván Castilla
 *
 */
public class AnnualRiskBasedTimeToMultipleEventParam extends MultipleEventParam<Long> implements TimeToEventParam {
	/** -1/(annual risk of the event) */
	private final double minusAvgTimeToEvent; 
	/** Relative risk calculator */
	private final RRCalculator rr;

	/**
	 * 
	 */
	public AnnualRiskBasedTimeToMultipleEventParam(RandomNumber rng, int nPatients, double annualRisk, RRCalculator rr) {
		super(rng, nPatients);
		minusAvgTimeToEvent = -1 / annualRisk;
		this.rr = rr;
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.params.Param#getValue(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public Long getValue(DiabetesPatient pat) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		if (Double.isInfinite(minusAvgTimeToEvent))
			return Long.MAX_VALUE;
		final double newMinus = -1 / (1-Math.exp(Math.log(1+1/minusAvgTimeToEvent)*rr.getRR(pat)));
		final double time = newMinus * Math.log(draw(pat));		
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}

}
