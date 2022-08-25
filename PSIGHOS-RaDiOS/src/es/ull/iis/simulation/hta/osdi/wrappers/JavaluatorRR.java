/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class JavaluatorRR implements RRCalculator {
	private final String expression;
	/**
	 * 
	 */
	public JavaluatorRR(String expression) {
		this.expression = expression;
	}

	@Override
	public double getRR(Patient pat) {
		final StaticVariableSet<Double> vars = new JavaluatorPatient(pat);
		return new DoubleEvaluator().evaluate(expression, vars);
	}

}
