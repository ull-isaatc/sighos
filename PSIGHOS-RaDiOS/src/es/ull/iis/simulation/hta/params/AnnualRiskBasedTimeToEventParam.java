/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
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
	public Long getValue(Patient pat) {
		return SecondOrderParamsRepository.getAnnualBasedTimeToEvent(pat, annualRisk, draw(pat), rr.getRR(pat));
	}
}
