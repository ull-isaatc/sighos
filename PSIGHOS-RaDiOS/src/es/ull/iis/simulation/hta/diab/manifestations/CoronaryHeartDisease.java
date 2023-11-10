/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

/**
 * @author Iván Castilla
 *
 */
public class CoronaryHeartDisease extends DiseaseProgression {
	public static final String NAME = "CHD";

	/**
	 * @param secParams
	 * @param disease
	 */
	public CoronaryHeartDisease(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, NAME, "Coronary Heart Disease", disease, Type.STAGE);
	}

}
