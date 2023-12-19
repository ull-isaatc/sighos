/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.StandardParameter;

/**
 * @author Iván Castilla
 *
 */
public class HealthTechnology extends HTAModelComponent implements PartOfStrategy {
	private final Guideline guide;
	/**
	 * 
	 */
	public HealthTechnology(HTAModel model, String name, String description, Guideline guide) {
		super(model, name, description);
		this.guide = guide;
		addUsedParameter(StandardParameter.ANNUAL_COST);
		addUsedParameter(StandardParameter.UNIT_COST);
	}

	public double getUnitCost(Patient pat) {
		return  getUsedParameterValue(StandardParameter.UNIT_COST, pat);
	}

	/**
	 * @return the guide
	 */
	public Guideline getGuide() {
		return guide;
	}

	@Override
	public double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate) {
		double cost =  getUsedParameterValue(StandardParameter.ANNUAL_COST, pat);
		// If there is an annual cost defined, ignores the guideline
		if (!Double.isNaN(cost))
			return discountRate.applyDiscount(cost, startT, endT);
		// Otherwise, looks for a unit cost to apply a guideline
		cost =  getUsedParameterValue(StandardParameter.UNIT_COST, pat);
		if (!Double.isNaN(cost))
			return guide.getCost(cost, startT, endT, discountRate);
		return 0.0;
	}

	@Override
	public double[] getAnnualizedCostForPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO Auto-generated method stub
		return null;
	}

}
