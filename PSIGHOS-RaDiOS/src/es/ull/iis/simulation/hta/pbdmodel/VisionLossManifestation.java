/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
		StandardParameter.DISEASE_PROGRESSION_ONSET_AGE.addParameter(getModel(), this, "", 1.0);
		StandardParameter.DISEASE_PROGRESSION_END_AGE.addParameter(getModel(), this, "", 2.0);
		StandardParameter.DISEASE_DIAGNOSIS_COST.addParameter(getModel(), this, "Test", COST_YEAR, DIAGNOSTIC_COST, SecondOrderParamsRepository.getRandomVariateForCost(DIAGNOSTIC_COST));
		StandardParameter.ANNUAL_COST.addParameter(getModel(), this, "Test", COST_YEAR, ANNUAL_COST, SecondOrderParamsRepository.getRandomVariateForCost(ANNUAL_COST));
		StandardParameter.DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS.addParameter(getModel(), this, "Assumption", 1.0);
	}

}
