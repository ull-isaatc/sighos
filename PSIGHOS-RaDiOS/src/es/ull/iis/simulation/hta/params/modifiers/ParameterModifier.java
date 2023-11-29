/**
 * 
 */
package es.ull.iis.simulation.hta.params.modifiers;

import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ParameterModifier {
	/** A dummy modification that do not modify anything */
	public final ParameterModifier NULL_MODIFIER = new ParameterModifier() {
		
		@Override
		public double getModifiedValue(Patient pat, double originalValue) {
			return originalValue;
		}
	};

	public double getModifiedValue(Patient pat, double originalValue);
}
