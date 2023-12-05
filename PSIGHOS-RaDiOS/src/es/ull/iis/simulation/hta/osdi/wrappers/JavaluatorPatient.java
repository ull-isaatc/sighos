/**
 * 
 */
package es.ull.iis.simulation.hta.osdi.wrappers;

import com.fathzer.soft.javaluator.AbstractVariableSet;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class JavaluatorPatient implements AbstractVariableSet<Double> {
	private final Patient pat;

	/**
	 * 
	 */
	public JavaluatorPatient(Patient pat) {
		this.pat = pat;
	}

	@Override
	public Double get(String variableName) {
		if ("AGE".equals(variableName))
			return pat.getAge();
		if ("SEX".equals(variableName))
			return (double) pat.getSex();
		if ("DIAGNOSED".equals(variableName))
			return pat.isDiagnosed() ? 1.0 : 0.0;
		if ("INTERVENTION".equals(variableName))
			return (double) pat.getnIntervention();
		double paramValue = pat.getSimulation().getRepository().getParameterValue(variableName, pat);
		if (!Double.isNaN(paramValue))
			return paramValue;
		return Double.NaN;
	}

}
