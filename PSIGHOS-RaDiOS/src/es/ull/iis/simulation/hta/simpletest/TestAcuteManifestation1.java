/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
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
		StandardParameter.ONSET_DISUTILITY.addParameter(model, this, "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.05, DISUTILITY + 0.05));
		StandardParameter.DISEASE_PROGRESSION_RISK_OF_DEATH.addParameter(model, this, "Test", P_DEAD, StandardParameter.getRandomVariateForProbability(P_DEAD));
		StandardParameter.ONSET_COST.addParameter(model, this, "Test", PUNCTUAL_COST, StandardParameter.getRandomVariateForCost(PUNCTUAL_COST));
		StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS.addParameter(model, this, "Test", P_DIAG, StandardParameter.getRandomVariateForProbability(P_DIAG));
	}

}
