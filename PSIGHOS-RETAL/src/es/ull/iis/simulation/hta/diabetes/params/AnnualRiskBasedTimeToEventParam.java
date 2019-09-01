/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.RandomNumber;

/**
 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla
 *
 */
public class AnnualRiskBasedTimeToEventParam extends UniqueEventParam<Long> implements TimeToEventParam {
	/** Annual risk of the event */
	private final double annualRisk; 
	/** Relative risk calculator */
	private final RRCalculator rr;

	/**
	 * 
	 * @param rng Random number generator
	 * @param nPatients Number of simulated patients
	 * @param annualRisk Annual risk of the event
	 * @param rr Relative risk for the patient
	 */
	public AnnualRiskBasedTimeToEventParam(RandomNumber rng, int nPatients, double annualRisk, RRCalculator rr) {
		super(rng, nPatients, true);
		this.annualRisk = annualRisk;
		this.rr = rr;
	}

	@Override
	public Long getValue(DiabetesPatient pat) {
		if (annualRisk == 0)
			return Long.MAX_VALUE;
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
//		final double newMinus = -1 / (1-Math.exp(Math.log(1-annualRisk)*rr.getRR(pat)));
		final double newMinus = -1 / (1-Math.pow(1-annualRisk, rr.getRR(pat)));
		final double time = newMinus * draw(pat);		
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
}
