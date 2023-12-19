/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
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
		addParameter(StandardParameter.INCREASED_MORTALITY_RATE, "", "Test", IMR, RandomVariateFactory.getInstance("UniformVariate", IMR - 1, IMR + 1));
		addParameter(StandardParameter.ANNUAL_COST, "", "Test", HTAModel.getStudyYear(), ANNUAL_COST, StandardParameter.getRandomVariateForCost(ANNUAL_COST));
		addParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DISUTILITY, RandomVariateFactory.getInstance("UniformVariate", DISUTILITY - 0.1, DISUTILITY + 0.1));
		addParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Test", P_DIAG, StandardParameter.getRandomVariateForProbability(P_DIAG));
	}

}
