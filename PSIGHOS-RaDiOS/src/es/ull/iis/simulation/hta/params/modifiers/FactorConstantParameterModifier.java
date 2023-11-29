/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class FactorConstantParameterModifier implements ParameterModifier {
	final private double value;

	/**
	 * 
	 */
	public FactorConstantParameterModifier(double value) {
		this.value = value;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return originalValue * value;
	}

}
