package es.ull.iis.simulation.hta.T1DM;

public enum MainComplications implements Named {
	CHD("Coronary Heart Disease"),
	NEU("Neuropathy"),
	NPH("Nephropathy"),
	RET("Retinopathy");
	
	private final String description;
	
	private MainComplications(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}