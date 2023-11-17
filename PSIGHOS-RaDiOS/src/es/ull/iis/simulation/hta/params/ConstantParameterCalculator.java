/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class ConstantParameterCalculator implements ParameterCalculator {
	public final static ConstantParameterCalculator ZERO_PARAMETER_CALCULATOR = new ConstantParameterCalculator(0);
	
	private final double value;
	/**
	 * 
	 */
	public ConstantParameterCalculator(double value) {
		this.value = value;
	}

	@Override
	public double getValue(Patient pat) {
		return value;
	}

	public double getValue() {
		return value;
	}
}
