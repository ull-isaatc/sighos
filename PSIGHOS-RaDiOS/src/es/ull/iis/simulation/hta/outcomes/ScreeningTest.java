/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.params.DefinesSensitivityAndSpecificity;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ScreeningTest extends HealthTechnology implements DefinesSensitivityAndSpecificity {

	/**
	 * @param name
	 * @param description
	 */
	public ScreeningTest(HTAModel model, String name, String description, Guideline guide) {
		super(model, name, description, guide);
	}

}
