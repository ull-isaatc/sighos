/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
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
		StandardParameter.DISEASE_PROGRESSION_ONSET_AGE.addParameter(getModel(), this, "", 1.0);
		StandardParameter.DISEASE_PROGRESSION_END_AGE.addParameter(getModel(), this, "", 2.0);
		StandardParameter.DISEASE_DIAGNOSIS_COST.addParameter(getModel(), this, "Test", COST_YEAR, DIAGNOSTIC_COST, StandardParameter.getRandomVariateForCost(DIAGNOSTIC_COST));
		StandardParameter.ANNUAL_COST.addParameter(getModel(), this, "Test", COST_YEAR, ANNUAL_COST, StandardParameter.getRandomVariateForCost(ANNUAL_COST));
		StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS.addParameter(getModel(), this, "Assumption", 1.0);
		StandardParameter.ANNUAL_DISUTILITY.addParameter(getModel(), this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
		StandardParameter.LIFE_EXPECTANCY_REDUCTION.addParameter(getModel(), this, "", 9.6, RandomVariateFactory.getInstance("GammaVariate", 25, 0.38));
	}
}
