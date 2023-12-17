/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
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
	 * @param model
	 * @param disease
	 */
	public TestManifestationStage1(HTAModel model, Disease disease) {
		super(model, "MANIF1", "Mild manifestation of test disease", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(model, this, "Test", 1.5, RandomVariateFactory.getInstance("UniformVariate", 1.3, 1.7));
		CostParamDescriptions.ANNUAL_COST.addParameter(model, this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		UtilityParamDescriptions.DISUTILITY.addParameter(model, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
	}

}
