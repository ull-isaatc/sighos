package es.ull.iis.simulation.hta.T1DM.params;

public enum Complication {
	CHD("Coronary Heart Disease"),
	NEU("Neuropathy"),
	NPH("Nephropathy"),
	RET("Retinopathy"),
	LEA("Lower Extremity Amputation"),
	ESRD("End-Stage Renal Disease"),
	BLI("Blindness");
	
	private final String description;
	
	private Complication(String description) {
		this.description = description;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}