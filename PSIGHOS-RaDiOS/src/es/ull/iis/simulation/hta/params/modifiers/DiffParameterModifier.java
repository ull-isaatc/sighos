/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class DiffParameterModifier implements ParameterModifier {
	final private String modifierParamName;

	/**
	 * 
	 */
	public DiffParameterModifier(String modifierParamName) {
		this.modifierParamName = modifierParamName;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return originalValue - pat.getSimulation().getRepository().getParameterValue(modifierParamName, pat);
	}

}
