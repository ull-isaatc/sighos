/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author masbe
 *
 */
public class CalculatedParameter extends Parameter {
	private final ParameterCalculator calc;	

	/**
	 * Creates a parameter whose value is calculated by using a {@link ParameterCalculator}
	 * @param secParams Common parameters repository
	 * @param name Short name and identifier of the parameter
	 * @param description Full description of the parameter
	 * @param source The reference from which this parameter was estimated/taken
	 * @param detValue Deterministic/expected value
	 * @param calc The way the value of the parameter is calculated
	 */
	public CalculatedParameter(SecondOrderParamsRepository secParams, DescribesParameter type, String name,
			String description, String source, ParameterCalculator calc) {
		super(secParams, type, name, description, source);
		this.calc = calc;
	}

	public ParameterCalculator getCalculator() {
		return calc;
	}
	
	@Override
	public double calculateValue(Patient pat) {
		return calc.getValue(pat);
	}
}
