/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.StandardDisease;

/**
 * A template for test diseases
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TemplateTestRareDisease extends StandardDisease {
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TemplateTestRareDisease(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public double getDiagnosisCost(Patient pat) {
		return 0;
	}

	@Override
	public double getAnnualTreatmentAndFollowUpCosts(Patient pat, double initAge, double endAge) {
		return 0;
	}
	
	public abstract ArrayList<String> getParamNames();
}
