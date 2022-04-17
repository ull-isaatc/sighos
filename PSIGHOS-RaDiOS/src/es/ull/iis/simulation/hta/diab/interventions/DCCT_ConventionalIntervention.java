/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class DCCT_ConventionalIntervention extends Intervention {
	private static final String NAME = "CONV";
	
	/**
	 * @param secParams
	 */
	public DCCT_ConventionalIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, NAME, NAME);
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return 0.0;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return 0.0;
	}

}
