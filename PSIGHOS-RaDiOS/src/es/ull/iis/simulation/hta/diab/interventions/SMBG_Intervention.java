/**
 * 
 */
package es.ull.iis.simulation.hta.diab.interventions;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.util.Statistics;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla
 *
 */
public class SMBG_Intervention extends Intervention {
	private static final String NAME = "SMBG";
	private static final double []USE_STRIPS = {4.6, 1.6};
	private static final double []MIN_MAX_USE_STRIPS = {3.0, 10.0};
	private static final String STR_USE_STRIPS = StandardParameter.RESOURCE_USAGE.createName("DAILY_USE_STRIPS_" + NAME);
	private static final String STR_C_STRIPS = StandardParameter.ANNUAL_COST.createName("STRIPS_" + NAME);
	private static final int YEAR_C_STRIPS = 2019;
	private static final double []C_STRIPS = {0.281378955, 0.098554232};
	private static final double []MIN_MAX_C_STRIPS = {0.1, 0.5};
	
	/**
	 * @param model
	 */
	public SMBG_Intervention(HTAModel model) {
		super(model, NAME, NAME);
	}

	@Override
	public void createParameters() {
		double mode = Statistics.betaModeFromMeanSD(USE_STRIPS[0], USE_STRIPS[1]);
		double[] betaParams = Statistics.betaParametersFromEmpiricData(USE_STRIPS[0], mode, MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[1]);
		RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		StandardParameter.RESOURCE_USAGE.addToModel(model, STR_USE_STRIPS, "Use of strips in " + getDescription(), 
				"DIAMOND 10.1001/jama.2016.19975", USE_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_USE_STRIPS[1] - MIN_MAX_USE_STRIPS[0], MIN_MAX_USE_STRIPS[0]));
		
		mode = Statistics.betaModeFromMeanSD(C_STRIPS[0], C_STRIPS[1]);
		betaParams = Statistics.betaParametersFromEmpiricData(C_STRIPS[0], mode, MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[1]);
		rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]);
		StandardParameter.ANNUAL_COST.addToModel(model, STR_C_STRIPS, "cost of strips", "Average from Spanish regions", 
				YEAR_C_STRIPS, C_STRIPS[0], RandomVariateFactory.getInstance("ScaledVariate", rnd, MIN_MAX_C_STRIPS[1] - MIN_MAX_C_STRIPS[0], MIN_MAX_C_STRIPS[0]));
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		final HTAModel model = getModel();

		return discountRate.applyDiscount(
			model.getParameterValue(STR_C_STRIPS, pat) *
			model.getParameterValue(STR_USE_STRIPS, pat) * 365, 
			initT, endT);
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return 0.0;
	}

	@Override
	public double[] getAnnualizedCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		final HTAModel model = getModel();
		return discountRate.applyAnnualDiscount(
			model.getParameterValue(STR_C_STRIPS, pat) *
			model.getParameterValue(STR_USE_STRIPS, pat) * 365, 
			initT, endT);
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
