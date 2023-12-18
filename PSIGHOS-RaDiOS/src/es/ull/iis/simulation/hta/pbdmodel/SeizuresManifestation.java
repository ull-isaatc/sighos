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
public class SeizuresManifestation extends DiseaseProgression {
	private final static int COST_YEAR = 2013;
	private final static double COST = 3665.56;
	private final static double DU = 0.04;

	/**
	 * @param secParams
	 * @param disease
	 */
	public SeizuresManifestation(HTAModel model, Disease disease) {
		super(model, "#PBD_ManifestationSeizure", "Seizures", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		StandardParameter.DISEASE_PROGRESSION_ONSET_AGE.addParameter(getModel(), this, "", 0.0);
		StandardParameter.DISEASE_PROGRESSION_END_AGE.addParameter(getModel(), this, "", 1.0);
		StandardParameter.ONSET_COST.addParameter(getModel(), this, "Test", COST_YEAR, COST, StandardParameter.getRandomVariateForCost(COST));
		StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS.addParameter(getModel(), this, "Assumption", 1.0);
		StandardParameter.ANNUAL_DISUTILITY.addParameter(getModel(), this, "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
	}

}
