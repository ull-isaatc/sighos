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
public interface CanDefineSpecificity extends CanDefineSecondOrderParameter, Describable, Named {
	final static String [] STR_SPECIFICITY = {"Specificity_", "Specificity for "};

	/**
	 * Returns a string to identify/describe the specificity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the specificity parameter associated to this disease
	 */
	public default String getSpecificityParameterString(boolean longText) {
		return longText ? (STR_SPECIFICITY[1] + getDescription()) : (STR_SPECIFICITY[0] + name());
	}

	/**
	 * @return the specificity
	 */
	public default double getSpecificity(Patient pat) {
		return getRepository().getProbParam(getSpecificityParameterString(false), pat.getSimulation());
	}
	
}
