/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;

/**
 * @author Iván Castilla
 *
 */
public class ExpressionLanguageCondition extends Condition<Patient> {
	
	private final JexlExpression exprToEvaluate;
	/**
	 * 
	 */
	public ExpressionLanguageCondition(String expression) {
		exprToEvaluate = OSDiGenericRepository.JEXL.createExpression(expression);
	}
	
	@Override
	public boolean check(Patient pat) {
		final MapContext jc = new ExpressionLanguagePatient(pat);
		boolean result = false;
		try {
			result = (boolean) exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
