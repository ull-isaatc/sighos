/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.modifiers.ParameterModifier;
import es.ull.iis.simulation.hta.params.modifiers.SetConstantParameterModifier;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDNewbornScreening extends ScreeningIntervention {
	private final static double C_TEST = 0.89;

	/**
	 * @param secParams
	 */
	public PBDNewbornScreening(HTAModel model) {
		super(model, "#PBD_InterventionScreening", "Basic screening");
	}

	@Override
	public void createParameters() {
		addParameter(StandardParameter.ONE_TIME_COST, "", "", 2013, C_TEST, RandomVariateFactory.getInstance("UniformVariate", 0.5, 2.5));
		addParameter(StandardParameter.SENSITIVITY, "", "", 1.0);
		addParameter(StandardParameter.SPECIFICITY, "", "", 0.999935);
		final ParameterModifier modifier = new SetConstantParameterModifier(0.0); 
		for (DiseaseProgression manif : model.getRegisteredDiseaseProgressions())
			model.addParameterModifier(StandardParameter.PROPORTION.createName(manif), this, modifier);
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0.0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(getUsedParameterValue(StandardParameter.ONE_TIME_COST, pat), time);
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
