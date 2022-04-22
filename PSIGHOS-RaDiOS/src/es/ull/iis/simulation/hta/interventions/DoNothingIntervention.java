/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.Patient;
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
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0;
	}

}
