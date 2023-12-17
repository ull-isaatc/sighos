/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import simkit.random.RandomVariateFactory;

/**
 * A recurrent acute manifestation with 10% mortality 
 * @author Iván Castilla
 *
 */
public class TestAcuteManifestation1 extends DiseaseProgression {
	private final static double PUNCTUAL_COST = 1000.0;
	private final static double DISUTILITY = 0.2;
	private final static double P_DEAD = 0.1;
	private final static double P_DIAG = 0.25;

	/**
	 * @param model
	 * @param disease
	 */
	public TestAcuteManifestation1(HTAModel model, Disease disease) {
		super(model, "ACUTE1", "Acute manifestation of test disease", disease, Type.ACUTE_MANIFESTATION);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.CreatesSecondOrderParameters#registerSecondOrderParameters()
	 */
	@Override
	public void createParameters() {
		UtilityParamDescriptions.ONE_TIME_DISUTILITY.addParameter(model, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
		RiskParamDescriptions.PROBABILITY_DEATH.addParameter(model, this, "Test", P_DEAD, SecondOrderParamsRepository.getRandomVariateForProbability(P_DEAD));
		CostParamDescriptions.ONE_TIME_COST.addParameter(model, this, "Test", 2020, PUNCTUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(PUNCTUAL_COST));		
		RiskParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(model, this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
