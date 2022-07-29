/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class MortalityReductionIntervention extends Intervention {
	private final static double ANNUAL_COST = 200.0; 

	/**
	 * @param secParams
	 */
	public MortalityReductionIntervention(SecondOrderParamsRepository secParams, Modification.Type type) {
		super(secParams, "MORT_RED", "Intervention that reduces mortality");
		double value = 0.0;
		switch(type) {
		case DIFF:
			// We increase 5 years the life expectancy
			value = -5.0;
			break;
		case RR:
			// We increase lifetime by a factor of 1.2
			value = 1.2;
			break;
		case SET:
			// We set lifetime to 100 years
			value = 90.0;
			break;
		default:
			break;		
		}
		setLifeExpectancyModification(new Modification(secParams, type, "MOD_MORT1", "Reduction of mortality due to intervention", "", value));
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
