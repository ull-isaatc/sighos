/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomNumber;

/**
 * A parameter based on annual risks that can be used to calculate multiple times to events
 * @author Iván Castilla
 *
 */
public class AnnualRiskBasedTimeToMultipleEventParam extends MultipleEventParam<Long> implements TimeToEventParam {
	/** annual risk of the event */
	private final double annualRisk; 
	/** Relative risk calculator */
	private final RRCalculator rr;

	/**
	 * 
	 */
	public AnnualRiskBasedTimeToMultipleEventParam(RandomNumber rng, int nPatients, double annualRisk, RRCalculator rr) {
		super(rng, nPatients, true);
		this.annualRisk = annualRisk;
		this.rr = rr;
	}

	@Override
	public Long getValue(Patient pat) {
		return SecondOrderParamsRepository.getAnnualBasedTimeToEvent(pat, annualRisk, draw(pat), rr.getRR(pat));
	}

}
