package es.ull.iis.simulation.hta.T1DM;

/**
 * Main chronic complications included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public enum T1DMChronicComplications implements Named {
	CHD("Coronary Heart Disease"),
	NEU("Neuropathy"),
	NPH("Nephropathy"),
	RET("Retinopathy");
	
	private final String description;
	
	private T1DMChronicComplications(String description) {
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