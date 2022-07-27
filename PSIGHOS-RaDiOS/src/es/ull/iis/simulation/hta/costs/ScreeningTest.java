/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.params.DefinesSensitivityAndSpecificity;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iv�n Castilla Rodr�guez
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