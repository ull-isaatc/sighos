/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.osdi.OSDiGenericRepository;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;

/**
 * @author Iván Castilla
 *
 */
public class ExpressionLanguageCondition extends Condition<DiseaseProgressionPathway.ConditionInformation> {
	
	private final JexlExpression exprToEvaluate;
	/**
	 * 
	 */
	public ExpressionLanguageCondition(String expression) {
		exprToEvaluate = OSDiGenericRepository.JEXL.createExpression(expression);
	}
	
	@Override
	public boolean check(DiseaseProgressionPathway.ConditionInformation info) {
		final JexlContext jc = new ExpressionLanguagePatient(info.getPatient());
		boolean result = false;
		try {
			result = (boolean) exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
