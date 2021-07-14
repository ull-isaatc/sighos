/**
 * 
 */
package es.ull.iis.simulation.hta.diab.manifestations;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.Manifestation;

/**
 * @author Iván Castilla
 *
 */
public class SevereHypoglycemiaEvent extends Manifestation {
	
	/**
	 * @param secParams
	 * @param disease
	 */
	public SevereHypoglycemiaEvent(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "SHE", "Severe hypoglycemic event", disease, Type.ACUTE);
	}

	@Override
	public void registerSecondOrderParameters() {
		// TODO Auto-generated method stub

	}

}
