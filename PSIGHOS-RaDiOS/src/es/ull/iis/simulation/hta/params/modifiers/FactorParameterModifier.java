/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla
 *
 */
public class FactorParameterModifier implements ParameterModifier {
	final private String modifierParamName;

	/**
	 * 
	 */
	public FactorParameterModifier(String modifierParamName) {
		this.modifierParamName = modifierParamName;
	}

	@Override
	public double getModifiedValue(Patient pat, double originalValue) {
		return originalValue * pat.getSimulation().getModel().getParameterValue(modifierParamName, pat);
	}

}
