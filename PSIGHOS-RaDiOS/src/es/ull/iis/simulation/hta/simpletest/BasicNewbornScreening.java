/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BasicNewbornScreening extends ScreeningIntervention {
	private final static double C_TEST = 5.0;

	/**
	 * @param secParams
	 */
	public BasicNewbornScreening(SecondOrderParamsRepository secParams) {
		super(secParams, "SCREEN", "Basic screening");
	}

	@Override
	public void registerSecondOrderParameters() {
		DefaultProbabilitySecondOrderParam.SENSITIVITY.addParameter(secParams, this, this, "", 0.9);
		DefaultProbabilitySecondOrderParam.SPECIFICTY.addParameter(secParams, this, this, "", 0.9);
	}

	@Override
	public double getAnnualCost(Patient pat) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat) {
		return C_TEST;
	}

}
