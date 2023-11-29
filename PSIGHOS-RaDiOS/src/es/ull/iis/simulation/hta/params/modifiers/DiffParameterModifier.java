/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.Parameter;

/**
 * @author Iván Castilla
 *
 */
public class DiffParameterModifier implements ParameterModifier {
	final private Parameter modifier;

	/**
	 * 
	 */
	public DiffParameterModifier(Parameter modifier) {
		this.modifier = modifier;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return originalValue - modifier.getValue(pat);
	}

}
