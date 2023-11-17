/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class SetParameterModifier implements ParameterModifier {
	final private Parameter modifier;
	/**
	 * 
	 */
	public SetParameterModifier(Parameter modifier) {
		this.modifier = modifier;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return modifier.getValue(pat);
	}

}
