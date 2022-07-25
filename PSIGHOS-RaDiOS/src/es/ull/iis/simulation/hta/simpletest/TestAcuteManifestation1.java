/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.AcuteManifestation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * A recurrent acute manifestation with 10% mortality 
 * @author Iván Castilla
 *
 */
public class TestAcuteManifestation1 extends AcuteManifestation {
	private final static double PUNCTUAL_COST = 1000.0;
	private final static double DISUTILITY = 0.2;
	private final static double P_DEAD = 0.1;
	private final static double P_DIAG = 0.25;

	/**
	 * @param secParams
	 * @param disease
	 */
	public TestAcuteManifestation1(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "ACUTE1", "Acute manifestation of test disease", disease);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.CreatesSecondOrderParameters#registerSecondOrderParameters()
	 */
	@Override
	public void registerSecondOrderParameters() {
		secParams.addUtilityParam(this, "Disutility for " + this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05), true);
		DefaultProbabilitySecondOrderParam.PROBABILITY_DEATH.addParameter(secParams, this, this, "Test", P_DEAD, SecondOrderParamsRepository.getRandomVariateForProbability(P_DEAD));
		secParams.addCostParam(this, "Punctual cost for " + this, "Test", 2020, PUNCTUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(PUNCTUAL_COST));		
		DefaultProbabilitySecondOrderParam.PROBABILITY_DIAGNOSIS.addParameter(secParams, this, this, "Test", P_DIAG, SecondOrderParamsRepository.getRandomVariateForProbability(P_DIAG));
	}

}
