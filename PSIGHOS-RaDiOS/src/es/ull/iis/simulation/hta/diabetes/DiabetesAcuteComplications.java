/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.Named;

/**
 * Main acute complications included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public enum DiabetesAcuteComplications implements Named {
	SHE("Severe hypoglycemic event");
	
	private final String description;
	
	private DiabetesAcuteComplications(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of the complication
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	
}
