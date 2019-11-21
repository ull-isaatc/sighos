/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.params;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import simkit.random.RandomNumber;

/**
 * A parameter based on annual rates that can be used to calculate multiple times to events
 * @author Iván Castilla
 *
 */
public class AnnualRateBasedTimeToMultipleEventParam extends MultipleEventParam<Long> implements TimeToEventParam {
	/** -1/(annual rate of the event) */
	private final double annualRate; 
	/** Incidence rate ratio calculator */
	private final RRCalculator irr;

	/**
	 * 
	 */
	public AnnualRateBasedTimeToMultipleEventParam(RandomNumber rng, int nPatients, double annualRate, RRCalculator irr) {
		super(rng, nPatients, true);
		this.annualRate = annualRate;
		this.irr = irr;
	}

	@Override
	public Long getValue(DiabetesPatient pat) {
		return SecondOrderParamsRepository.getAnnualBasedTimeToEventFromRate(pat, annualRate, draw(pat), irr.getRR(pat));
	}

}
