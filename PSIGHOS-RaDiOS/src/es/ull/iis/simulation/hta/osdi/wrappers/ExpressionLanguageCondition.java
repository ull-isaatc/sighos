/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class ExpressionLanguageCondition extends Condition<Patient> {
	private static final JexlEngine jexl = new JexlBuilder().create();
	
	final JexlExpression exprToEvaluate;
	/**
	 * 
	 */
	public ExpressionLanguageCondition(String expression) {
		exprToEvaluate = jexl.createExpression(expression);
	}
	
	@Override
	public boolean check(Patient pat) {
		final MapContext jc = pat.createJEXLContext();
		boolean result = false;
		try {
			result = (boolean) exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
