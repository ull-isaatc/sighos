/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class JavaluatorParameterCalculator implements ParameterCalculator {
	private final String expression;
	/**
	 * 
	 */
	public JavaluatorParameterCalculator(String expression) {
		this.expression = expression;
	}

	@Override
	public double getValue(Patient pat) {
		final StaticVariableSet<Double> vars = new JavaluatorPatient(pat);
		return new DoubleEvaluator().evaluate(expression, vars);
	}

}
