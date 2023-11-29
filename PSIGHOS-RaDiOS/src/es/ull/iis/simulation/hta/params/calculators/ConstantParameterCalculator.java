/**
 * 
 */
package es.ull.iis.simulation.hta.params.calculators;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author masbe
 *
 */
public class ConstantParameterCalculator implements ParameterCalculator {
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

}
