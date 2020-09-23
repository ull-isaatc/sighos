/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomNumber;

/**
 * Generates a time to event based on patients-year rate. The time to event is absolute, i.e., can be used directly to schedule a new event. 
 * @author Iván Castilla
 *
 */
public class AnnualRateBasedTimeToEventParam extends UniqueEventParam<Long> implements TimeToEventParam {
	/** Annual rate of the event */
	private final double annualRate; 
	/** Incidence rate ratio calculator */
	private final RRCalculator irr;

	/**
	 * 
	 * @param rng Random number generator
	 * @param nPatients Number of simulated patients
	 * @param annualRate Annual rate of the event
	 * @param irr Incidence rate ratio for the patient
	 */
	public AnnualRateBasedTimeToEventParam(RandomNumber rng, int nPatients, double annualRate, RRCalculator irr) {
		super(rng, nPatients, true);
		this.annualRate = annualRate;
		this.irr = irr;
	}

	@Override
	public Long getValue(Patient pat) {
		return SecondOrderParamsRepository.getAnnualBasedTimeToEventFromRate(pat, annualRate, draw(pat), irr.getRR(pat));
	}
}
