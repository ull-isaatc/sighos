/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class ConstantParameter extends Parameter {
	private final double value;
	/**
	 * 
	 */
	public ConstantParameter(final SecondOrderParamsRepository secParams, DescribesParameter type, String name, String description, String source, double value) {
		super(secParams, type, name, description, source);
		this.value = value;
	}

	@Override
	public double calculateValue(Patient pat) {
		return value;
	}

	public double getValue() {
		return value;
	}
}
