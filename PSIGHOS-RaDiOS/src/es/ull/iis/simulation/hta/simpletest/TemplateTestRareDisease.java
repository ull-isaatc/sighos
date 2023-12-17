/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * A template for test diseases
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class TemplateTestRareDisease extends Disease {
	
	/**
	 * @param secParams Repository with common information about the disease 
	 */
	public TemplateTestRareDisease(HTAModel model, String name, String description) {
		super(model, name, description);
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		double [] results = new double[(int)endT - (int)initT + 1];
		return results;
	}

	public abstract ArrayList<String> getParamNames();
}
