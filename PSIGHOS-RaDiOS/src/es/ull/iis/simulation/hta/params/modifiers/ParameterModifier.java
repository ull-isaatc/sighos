/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.interventions.Intervention;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ParameterModifier {
	public final String STR_MOD_PREFIX = "MOD_";
	/** A dummy modification that do not modify anything */
	public final ParameterModifier NULL_MODIFIER = new ParameterModifier() {
		
		@Override
		public double getModifiedValue(Patient pat, double originalValue) {
			return originalValue;
		}
	};

	public double getModifiedValue(Patient pat, double originalValue);

	/**
	 * Returns the name of the parameter that represents the modification of the specified parameter
	 * @param paramName
	 * @return
	 */
	public static String getModifierParamName(Intervention interv, String paramName) {
		return STR_MOD_PREFIX + interv.name() + "_" + paramName;
	}

}
