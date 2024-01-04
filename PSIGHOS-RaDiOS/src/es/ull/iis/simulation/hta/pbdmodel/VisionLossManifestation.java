/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.Parameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.Disease;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class VisionLossManifestation extends DiseaseProgression {
	private final static int COST_YEAR = 2013;
	private final static double DIAGNOSTIC_COST = 251.8;
	private final static double ANNUAL_COST = 121.86;

	/**
	 * @param secParams
	 * @param disease
	 */
	public VisionLossManifestation(HTAModel model, Disease disease) {
		super(model, "#PBD_ManifestationVisionLoss", "Vision loss", disease, Type.CHRONIC_MANIFESTATION);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_ONSET_AGE, "", "", 1.0);
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_END_AGE, "", "", 2.0);
		addUsedParameter(StandardParameter.DISEASE_DIAGNOSIS_COST, "", "Test", COST_YEAR, DIAGNOSTIC_COST, Parameter.getRandomVariateForCost(DIAGNOSTIC_COST));
		addUsedParameter(StandardParameter.ANNUAL_COST, "", "Test", COST_YEAR, ANNUAL_COST, Parameter.getRandomVariateForCost(ANNUAL_COST));
		addUsedParameter(StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS, "", "Assumption", 1.0);
	}

}
