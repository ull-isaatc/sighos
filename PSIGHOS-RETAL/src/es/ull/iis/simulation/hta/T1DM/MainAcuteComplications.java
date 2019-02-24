/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * @author Iv�n Castilla Rodr�guez
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
