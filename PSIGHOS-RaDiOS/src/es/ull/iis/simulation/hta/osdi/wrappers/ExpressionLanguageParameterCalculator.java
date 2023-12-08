/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.params.calculators.ParameterCalculator;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Convert into Parameter
 */
public class ExpressionLanguageParameterCalculator implements ParameterCalculator {
	private final JexlExpression exprToEvaluate;
	/**
	 * 
	 */
	public ExpressionLanguageParameterCalculator(String expression) {
		exprToEvaluate = OSDiGenericRepository.JEXL.createExpression(expression);
	}

	@Override
	public double getValue(Patient pat) {
		final JexlContext jc = new ExpressionLanguagePatient(pat);
		double result = 1.0;
		try {
			result = (double) exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
