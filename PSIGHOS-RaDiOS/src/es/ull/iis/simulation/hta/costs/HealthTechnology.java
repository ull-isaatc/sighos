/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.DefaultSecondOrderParam;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
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
		return secParams.getCostParam(DefaultSecondOrderParam.UNIT_COST.getName(this), pat.getSimulation());
	}

	/**
	 * @return the guide
	 */
	public Guideline getGuide() {
		return guide;
	}

	@Override
	public double getCostForPeriod(Patient pat, double startT, double endT, Discount discountRate) {
		final double unitCost = secParams.getCostParam(DefaultSecondOrderParam.UNIT_COST.getName(this), pat.getSimulation());
		final boolean isAnnual = SecondOrderCostParam.TemporalBehavior.ANNUAL.equals(secParams.getTemporalBehaviorOfCostParam(DefaultSecondOrderParam.UNIT_COST.getName(this)));
		// If the cost is annual, then the guideline is ignored
		if (isAnnual) {
			return discountRate.applyDiscount(unitCost, startT, endT);
		}
		// If the cost is punctual, the guideline is used
		return guide.getCost(unitCost, startT, endT, discountRate);
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
