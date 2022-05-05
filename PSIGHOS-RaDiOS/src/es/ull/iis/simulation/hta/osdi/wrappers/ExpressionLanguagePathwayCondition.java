/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlException;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.hta.progression.PathwayCondition;

/**
 * @author Iván Castilla
 *
 */
public class ExpressionLanguagePathwayCondition extends PathwayCondition {
	private static final JexlEngine jexl = new JexlBuilder().create();
	
	final JexlExpression exprToEvaluate;
	/**
	 * 
	 */
	public ExpressionLanguagePathwayCondition(String expression) {
		exprToEvaluate = jexl.createExpression(expression);
	}

	// FIXME: Add specific parameters and see what to do with this parameters
	private MapContext createContextFromPatient(Patient pat) {
		final MapContext jc = new MapContext();
		jc.set("age", pat.getAge());
		jc.set("sex", pat.getSex());
		jc.set("disease", pat.getDisease().name());
		jc.set("intervention", pat.getIntervention().name());
		for (Manifestation manif : pat.getState()) {
			jc.set(manif.name(), pat.getTimeToManifestation(manif));
		}
		return jc;
	}
	
	@Override
	public boolean check(Patient pat) {
		final MapContext jc = createContextFromPatient(pat);
		boolean result = false;
		try {
			result = (boolean) exprToEvaluate.evaluate(jc);
		} catch (JexlException ex) {
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
