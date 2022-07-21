/**
 * 
 */
package es.ull.iis.simulation.hta.costs;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla
 *
 */
public class ScreeningTest extends HealthTechnology {
	private final static String [] STR_SPECIFICITY = {"Specificity_", "Specificity for "};
	private final static String [] STR_SENSITIVITY = {"Sensitivity_", "Sensitivity for "};

	/**
	 * @param name
	 * @param description
	 */
	public ScreeningTest(SecondOrderParamsRepository secParams, String name, String description, Guideline guide) {
		super(secParams, name, description, guide);
	}
	
	/**
	 * Returns a string to identify/describe the specificity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the specificity parameter associated to this disease
	 */
	public String getSpecificityParameterString(boolean longText) {
		return longText ? (STR_SPECIFICITY[1] + getDescription()) : (STR_SPECIFICITY[0] + name());
	}
	
	/**
	 * Returns a string to identify/describe the sensitivity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the sensitivity parameter associated to this disease
	 */
	public String getSensitivityParameterString(boolean longText) {
		return longText ? (STR_SENSITIVITY[1] + getDescription()) : (STR_SENSITIVITY[0] + name());
	}
	
	/**
	 * @return the sensitivity
	 */
	public double getSensitivity(Patient pat) {
		return secParams.getProbParam(getSpecificityParameterString(false), pat.getSimulation());
	}

	/**
	 * @return the specificity
	 */
	public double getSpecificity(Patient pat) {
		return secParams.getProbParam(getSensitivityParameterString(false), pat.getSimulation());
	}

}
