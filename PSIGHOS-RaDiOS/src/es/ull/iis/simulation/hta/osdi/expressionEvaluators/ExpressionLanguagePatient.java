/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.expressionEvaluators;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.jexl3.JexlContext;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ExpressionLanguagePatient implements JexlContext {
	private final Map<String, Object> fixedValues;
	private final Patient pat;
	
	/**
	 * 
	 */
	public ExpressionLanguagePatient(Patient pat) {
		this.pat = pat;
		fixedValues = new TreeMap<>();
		set("AGE", pat.getAge());
		set("SEX", pat.getSex());
		set("DIAGNOSED", pat.isDiagnosed());
		set("INTERVENTION", pat.getnIntervention());
	}


	@Override
	public Object get(String name) {
		if (fixedValues.containsKey(name))
			return fixedValues.get(name);
		double paramValue = pat.getSimulation().getModel().getParameterValue(name, pat);
		if (!Double.isNaN(paramValue))
			return paramValue;
		return Double.NaN;
	}

	@Override
	public void set(String name, Object value) {
		fixedValues.put(name, value);
	}

	@Override
	public boolean has(String name) {
		return fixedValues.containsKey(name) || pat.getSimulation().getModel().getParameters().containsKey(name);
	}

}
