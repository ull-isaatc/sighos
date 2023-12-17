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
	public enum USED_PARAMETERS implements UsedParameter {
		SENSITIVITY,
		SPECIFICITY
	}
	/**
	 * @param instance TODO
	 * @return the specificity
	 */
	public default double getSpecificity(HTAModelComponent instance, Patient pat) {
		return instance.getModel().getParameterValue(instance.getUsedParameterName(USED_PARAMETERS.SPECIFICITY), pat);
	}
	
	/**
	 * @param instance TODO
	 * @return the sensitivity
	 */
	public default double getSensitivity(HTAModelComponent instance, Patient pat) {
		return instance.getModel().getParameterValue(instance.getUsedParameterName(USED_PARAMETERS.SENSITIVITY), pat);
	}
}
