/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class MentalDelayManifestation extends DiseaseProgression {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 1218.02;
	private final static double ANNUAL_COST = 217.74;
	private final static double DU = 0.07;

	/**
	 * @param secParams
	 * @param disease
	 */
	public MentalDelayManifestation(HTAModel model, Disease disease) {
		super(model, "#PBD_ManifestationMentalDelay", "Mental delay", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_ONSET_AGE, "", "", 1.0);
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_END_AGE, "", "", 2.0);
		addUsedParameter(StandardParameter.DISEASE_DIAGNOSIS_COST, "", "Test", COST_YEAR, DIAGNOSTIC_COST, Parameter.getRandomVariateForCost(DIAGNOSTIC_COST));
		addUsedParameter(StandardParameter.ANNUAL_COST, "", "Test", COST_YEAR, ANNUAL_COST, Parameter.getRandomVariateForCost(ANNUAL_COST));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Assumption", 1.0);
		addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
		addUsedParameter(StandardParameter.LIFE_EXPECTANCY_REDUCTION, "", "", 9.6, RandomVariateFactory.getInstance("GammaVariate", 25, 0.38));
	}
}
