/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class VisionLossManifestation extends ChronicManifestation {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 251.8;
	private final static double ANNUAL_COST = 121.86;

	/**
	 * @param secParams
	 * @param disease
	 */
	public VisionLossManifestation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "#PBD_ManifestationVisionLoss", "Vision loss", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addOtherParam(new SecondOrderParam(secParams, getOnsetAgeParameterString(false), getOnsetAgeParameterString(true), "", 1.0));
		secParams.addOtherParam(new SecondOrderParam(secParams, getEndAgeParameterString(false), getEndAgeParameterString(true), "", 2.0));
		secParams.addCostParam(this, "Punctual cost for " + this, "Test", COST_YEAR, DIAGNOSTIC_COST, SecondOrderParamsRepository.getRandomVariateForCost(DIAGNOSTIC_COST), true);		
		secParams.addCostParam(this, "Annual cost for " + this, "Test", COST_YEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));		
		DefaultProbabilitySecondOrderParam.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, this, "Assumption", 1.0, RandomVariateFactory.getInstance("ConstantVariate", 1.0));
	}

}
