/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla	
 *
 */
public class DoNothingIntervention extends Intervention {

	/**
	 * @param secParams
	 */
	public DoNothingIntervention(SecondOrderParamsRepository secParams) {
		super(secParams, "NONE", "Do nothing");
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
	}

}
