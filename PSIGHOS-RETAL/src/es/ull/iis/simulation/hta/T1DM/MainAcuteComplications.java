/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * Main acute complications modelled
 * @author Iván Castilla Rodríguez
 *
 */
public enum MainAcuteComplications implements Named {
	SEVERE_HYPO("Severe hypoglycemic event");
	
	private final String description;
	
	private MainAcuteComplications(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	
}
