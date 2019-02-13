/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * Main acute complications included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public enum T1DMAcuteComplications implements Named {
	SEVERE_HYPO("Severe hypoglycemic event");
	
	private final String description;
	
	private T1DMAcuteComplications(String description) {
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
