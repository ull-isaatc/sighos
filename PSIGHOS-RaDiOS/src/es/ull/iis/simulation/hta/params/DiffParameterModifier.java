/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

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
