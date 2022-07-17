/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningStrategy;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BasicNewbornScreening extends ScreeningStrategy {
	private final static double C_TEST = 5.0;

	/**
	 * @param secParams
	 */
	public BasicNewbornScreening(SecondOrderParamsRepository secParams) {
		super(secParams, "SCREEN", "Basic screening");
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addProbParam(new SecondOrderParam(secParams, getSensitivityParameterString(false), getSensitivityParameterString(true), "", 0.9));
		secParams.addProbParam(new SecondOrderParam(secParams, getSpecificityParameterString(false), getSpecificityParameterString(true), "", 0.9));
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
