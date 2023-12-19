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
		addParameter(StandardParameter.DISEASE_PROGRESSION_ONSET_AGE, "", "", 0.0);
		addParameter(StandardParameter.DISEASE_PROGRESSION_END_AGE, "", "", 1.0);
		addParameter(StandardParameter.ONSET_COST, "", "Test", COST_YEAR, COST, StandardParameter.getRandomVariateForCost(COST));
		addParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Assumption", 1.0);
		addParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DU, RandomVariateFactory.getInstance("UniformVariate", DU*0.8, DU*1.2));
	}

}
