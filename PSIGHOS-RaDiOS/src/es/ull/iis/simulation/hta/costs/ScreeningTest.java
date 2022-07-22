/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.params.CanDefineSensitivity;
import es.ull.iis.simulation.hta.params.CanDefineSpecificity;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ScreeningTest extends HealthTechnology implements CanDefineSpecificity, CanDefineSensitivity {

	/**
	 * @param name
	 * @param description
	 */
	public ScreeningTest(SecondOrderParamsRepository secParams, String name, String description, Guideline guide) {
		super(secParams, name, description, guide);
	}

}
