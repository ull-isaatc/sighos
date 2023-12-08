/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import com.fathzer.soft.javaluator.AbstractVariableSet;
import com.fathzer.soft.javaluator.DoubleEvaluator;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Convert into Parameter
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
		final AbstractVariableSet<Double> vars = new JavaluatorPatient(pat);
		return new DoubleEvaluator().evaluate(expression, vars);
	}

}
