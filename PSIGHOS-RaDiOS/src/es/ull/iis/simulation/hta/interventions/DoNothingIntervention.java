/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class DoNothingIntervention extends Intervention {

	/**
	 * @param secParams
	 */
	public DoNothingIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, "NONE", "Do nothing");
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0;
	}

}
