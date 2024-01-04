/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
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
		addUsedParameter(StandardParameter.ONSET_DISUTILITY, "", "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH, "", "Test", P_DEAD, Parameter.getRandomVariateForProbability(P_DEAD));
		addUsedParameter(StandardParameter.ONSET_COST, "", "Test", PUNCTUAL_COST, Parameter.getRandomVariateForCost(PUNCTUAL_COST));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Test", P_DIAG, Parameter.getRandomVariateForProbability(P_DIAG));
	}

}
