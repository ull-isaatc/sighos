/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.ChronicManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class TestManifestationStage2 extends ChronicManifestation {
	private final static double ANNUAL_COST = 1000.0;
	private final static double DISUTILITY = 0.5;
	private final static double IMR = 5.0;
	private final static double P_DIAG = 0.8;

	/**
	 * @param secParams
	 * @param disease
	 */
	public TestManifestationStage2(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "MANIF2", "Severe manifestation of test disease", disease);
	}

	@Override
	public void registerSecondOrderParameters() {
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, this, "Test", IMR, RandomVariateFactory.getInstance("UniformVariate", IMR - 1, IMR + 1));
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		secParams.addUtilityParam(this, "Disutility for " + this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.1, DISUTILITY + 0.1), true);
		ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
