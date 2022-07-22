/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
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
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
			Discount discountRate) {
		return 0;
	}

	@Override
	public double getDiagnosisCost(Patient pat, double time, Discount discountRate) {
		return 0;
	}

	public abstract ArrayList<String> getParamNames();
}
