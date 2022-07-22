/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.Describable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface CanDefineSensitivity extends CanDefineSecondOrderParameter, Describable, Named {
	final static String [] STR_SENSITIVITY = {"Sensitivity_", "Sensitivity for "};
	
	/**
	 * Returns a string to identify/describe the sensitivity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the sensitivity parameter associated to this disease
	 */
	public default String getSensitivityParameterString(boolean longText) {
		return longText ? (STR_SENSITIVITY[1] + getDescription()) : (STR_SENSITIVITY[0] + name());
	}
	
	/**
	 * @return the sensitivity
	 */
	public default double getSensitivity(Patient pat) {
		return getRepository().getProbParam(getSensitivityParameterString(false), pat.getSimulation());
	}

}
