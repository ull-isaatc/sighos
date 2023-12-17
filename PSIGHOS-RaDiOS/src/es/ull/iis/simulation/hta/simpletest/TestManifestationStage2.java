/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.OtherParamDescriptions;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestManifestationStage2 extends DiseaseProgression {
	private final static double ANNUAL_COST = 1000.0;
	private final static double DISUTILITY = 0.5;
	private final static double IMR = 5.0;
	private final static double P_DIAG = 0.8;

	/**
	 * @param model
	 * @param disease
	 */
	public TestManifestationStage2(HTAModel model, Disease disease) {
		super(model, "MANIF2", "Severe manifestation of test disease", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		OtherParamDescriptions.INCREASED_MORTALITY_RATE.addParameter(model, this, "Test", IMR, RandomVariateFactory.getInstance("UniformVariate", IMR - 1, IMR + 1));
		CostParamDescriptions.ANNUAL_COST.addParameter(model, this, "Test", 2020, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		UtilityParamDescriptions.DISUTILITY.addParameter(model, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.1, DISUTILITY + 0.1));
		RiskParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(model, this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
