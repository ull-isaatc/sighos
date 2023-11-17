/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class DiffConstantParameterModifier implements ParameterModifier {
	final private double value;

	/**
	 * 
	 */
	public DiffConstantParameterModifier(double value) {
		this.value = value;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return originalValue - value;
	}

}
