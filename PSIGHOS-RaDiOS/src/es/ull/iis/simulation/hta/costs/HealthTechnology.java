/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iv�n Castilla
 *
 */
public class HealthTechnology implements PartOfStrategy {
	private final String name;
	private final String description;
	private final SecondOrderParamsRepository secParams;
	private final Guideline guide;
	/**
	 * 
	 */
	public HealthTechnology(SecondOrderParamsRepository secParams, String name, String description, Guideline guide) {
		this.name = name;
		this.description = description;
		this.secParams = secParams;
		this.guide = guide;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public String name() {
		return name;
	}

	public double getUnitCost(Patient pat) {
		return CostParamDescriptions.UNIT_COST.getValue(secParams, this, pat.getSimulation());
	}

	/**
	 * @return the guide
	 */
	public Guideline getGuide() {
		return guide;
	}

	@Override
	public double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate) {
		double cost = CostParamDescriptions.ANNUAL_COST.getValueIfExists(secParams, this, pat.getSimulation());
		// If there is an annual cost defined, ignores the guideline
		if (!Double.isNaN(cost))
			return discountRate.applyDiscount(cost, startT, endT);
		// Otherwise, looks for a unit cost to apply a guideline
		cost = CostParamDescriptions.UNIT_COST.getValueIfExists(secParams, this, pat.getSimulation());
		if (!Double.isNaN(cost))
			return guide.getCost(cost, startT, endT, discountRate);
		return 0.0;
	}

	@Override
	public double[] getAnnualizedCostForPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SecondOrderParamsRepository getRepository() {
		return secParams;
	}

}