/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * @author Iván Castilla
 *
 */
public class CoronaryHeartDisease extends DiseaseProgression {
	public static final String NAME = "CHD";

	/**
	 * @param model
	 * @param disease
	 */
	public CoronaryHeartDisease(HTAModel model, Disease disease) {
		super(model, NAME, "Coronary Heart Disease", disease, Type.STAGE);
	}

}
