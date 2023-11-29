/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MortalityReductionIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 

	/**
	 * @param secParams
	 */
	public MortalityReductionIntervention(SecondOrderParamsRepository secParams, ParameterModifier modifier) {
		super(secParams, "MORT_RED", "Intervention that reduces mortality");
		setLifeExpectancyModification(modifier);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {

	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyDiscount(ANNUAL_COST, initT, endT);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0;
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyAnnualDiscount(ANNUAL_COST, initT, endT);
	}

	@Override
	public double getTreatmentAndFollowUpCosts(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double[] getAnnualizedTreatmentAndFollowUpCosts(Patient pat, double initT, double endT,
			Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
	}

}
