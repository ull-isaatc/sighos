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
		return ProbabilityParamDescriptions.SPECIFICTY.getValueIfExists(getRepository(), this, pat.getSimulation());
	}
	
	/**
	 * @return the sensitivity
	 */
	public default double getSensitivity(Patient pat) {
		return ProbabilityParamDescriptions.SENSITIVITY.getValueIfExists(getRepository(), this, pat.getSimulation());
	}
}
