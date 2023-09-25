/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.params.DefinesSensitivityAndSpecificity;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ScreeningTest extends HealthTechnology implements DefinesSensitivityAndSpecificity {

	/**
	 * @param name
	 * @param description
	 */
	public ScreeningTest(SecondOrderParamsRepository secParams, String name, String description, Guideline guide) {
		super(secParams, name, description, guide);
	}

}
