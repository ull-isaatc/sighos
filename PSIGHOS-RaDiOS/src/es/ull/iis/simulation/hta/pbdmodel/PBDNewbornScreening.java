/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.ScreeningIntervention;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.Modification;
import es.ull.iis.simulation.hta.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class PBDNewbornScreening extends ScreeningIntervention {
	private final static double C_TEST = 0.89;

	/**
	 * @param secParams
	 */
	public PBDNewbornScreening(SecondOrderParamsRepository secParams) {
		super(secParams, "#PBD_InterventionScreening", "Basic screening");
	}

	@Override
	public void registerSecondOrderParameters() {
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "", 2013, C_TEST, RandomVariateFactory.getInstance("UniformVariate", 0.5, 2.5));
		ProbabilityParamDescriptions.SENSITIVITY.addParameter(secParams, this, "", 1.0);
		ProbabilityParamDescriptions.SPECIFICTY.addParameter(secParams, this, "", 0.999935);
		for (Manifestation manif : secParams.getRegisteredManifestations())
			secParams.addModificationParam(this, Modification.Type.SET, ProbabilityParamDescriptions.PROBABILITY.getParameterName(manif), "", 0.0, RandomVariateFactory.getInstance("ConstantVariate", 0.0));
	}

	@Override
	public double getCostWithinPeriod(Patient pat, double initT, double endT, Discount discountRate) {
		return 0.0;
	}

	@Override
	public double getStartingCost(Patient pat, double time, Discount discountRate) {
		return discountRate.applyPunctualDiscount(CostParamDescriptions.ONE_TIME_COST.getValue(secParams, this, pat.getSimulation()), time);
	}

}
