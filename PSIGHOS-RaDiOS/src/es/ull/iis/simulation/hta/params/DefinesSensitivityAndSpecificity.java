/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModelComponent;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface DefinesSensitivityAndSpecificity {
	/**
	 * @param instance A model component
	 * @return the specificity
	 */
	public default double getSpecificity(HTAModelComponent instance, Patient pat) {
		return instance.getUsedParameterValue(StandardParameter.SPECIFICITY, pat);
	}
	
	/**
	 * @param instance A model component
	 * @return the sensitivity
	 */
	public default double getSensitivity(HTAModelComponent instance, Patient pat) {
		return instance.getUsedParameterValue(StandardParameter.SENSITIVITY, pat);
	}
}
