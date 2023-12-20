/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class HypotoniaManifestation extends DiseaseProgression {
	private final static int COST_YEAR = 2013;
	private final static double COST = 3665.56;

	/**
	 * @param secParams
	 * @param disease
	 */
	public HypotoniaManifestation(HTAModel model, Disease disease) {
		super(model, "#PBD_ManifestationHypotonia", "Hypotonia", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_ONSET_AGE, "", "", 0.0);
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_END_AGE, "", "", 1.0);
		addUsedParameter(StandardParameter.ONSET_COST, "", "Test", COST_YEAR, COST, StandardParameter.getRandomVariateForCost(COST));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Assumption", 1.0);
	}

}
