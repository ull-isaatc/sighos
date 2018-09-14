package es.ull.iis.simulation.hta.T1DM;

/**
 * Main chronic complications modelled
 * @author Iván Castilla Rodríguez
 *
 */
public enum MainChronicComplications implements Named {
	CHD("Coronary Heart Disease"),
	NEU("Neuropathy"),
	NPH("Nephropathy"),
	RET("Retinopathy");
	
	private final String description;
	
	private MainChronicComplications(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}