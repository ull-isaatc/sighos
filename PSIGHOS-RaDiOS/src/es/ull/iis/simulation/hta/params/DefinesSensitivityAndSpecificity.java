/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface DefinesSensitivityAndSpecificity extends CreatesSecondOrderParameters, Named {
	/**
	 * @return the specificity
	 */
	public default double getSpecificity(Patient pat) {
		return RiskParamDescriptions.SPECIFICITY.getValueIfExists(getRepository(), this, pat);
	}
	
	/**
	 * @return the sensitivity
	 */
	public default double getSensitivity(Patient pat) {
		return RiskParamDescriptions.SENSITIVITY.getValueIfExists(getRepository(), this, pat);
	}
}
