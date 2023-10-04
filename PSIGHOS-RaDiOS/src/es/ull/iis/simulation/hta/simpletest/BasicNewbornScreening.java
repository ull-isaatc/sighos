/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
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
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		ProbabilityParamDescriptions.SENSITIVITY.addParameter(secParams, this, "", 0.9);
		ProbabilityParamDescriptions.SPECIFICITY.addParameter(secParams, this, "", 0.9);
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(C_TEST, time);
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return discountRate.applyAnnualDiscount(0.0, initT, endT);
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
