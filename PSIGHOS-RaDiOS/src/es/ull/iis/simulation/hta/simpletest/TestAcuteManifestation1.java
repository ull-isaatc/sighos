/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.CostParamDescriptions;
import es.ull.iis.simulation.hta.params.ProbabilityParamDescriptions;
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
	 * @param secParams
	 * @param disease
	 */
	public TestAcuteManifestation1(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "ACUTE1", "Acute manifestation of test disease", disease, Type.ACUTE_MANIFESTATION);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.CreatesSecondOrderParameters#registerSecondOrderParameters()
	 */
	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		UtilityParamDescriptions.ONE_TIME_DISUTILITY.addParameter(secParams, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
		ProbabilityParamDescriptions.PROBABILITY_DEATH.addParameter(secParams, this, "Test", P_DEAD, SecondOrderParamsRepository.getRandomVariateForProbability(P_DEAD));
		CostParamDescriptions.ONE_TIME_COST.addParameter(secParams, this, "Test", 2020, PUNCTUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(PUNCTUAL_COST));		
		ProbabilityParamDescriptions.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
