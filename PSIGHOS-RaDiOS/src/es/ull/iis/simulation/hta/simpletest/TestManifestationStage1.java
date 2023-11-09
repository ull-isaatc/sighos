/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestManifestationStage1 extends DiseaseProgression {
	private final static double ANNUAL_COST = 100.0;
	private final static double DISUTILITY = 0.2;

	/**
	 * @param secParams
	 * @param disease
	 */
	public TestManifestationStage1(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "MANIF1", "Mild manifestation of test disease", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(secParams, this, "Test", 1.5, RandomVariateFactory.getInstance("UniformVariate", 1.3, 1.7));
		CostParamDescriptions.ANNUAL_COST.addParameter(secParams, this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		UtilityParamDescriptions.DISUTILITY.addParameter(secParams, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
	}

}
