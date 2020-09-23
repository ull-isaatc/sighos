package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.Named;

/**
 * Main chronic complications included in the model
 * @author Iván Castilla Rodríguez
 *
 */
public enum DiabetesChronicComplications implements Named {
	CHD("Coronary Heart Disease"),
	NEU("Neuropathy"),
	NPH("Nephropathy"),
	RET("Retinopathy");
	
	private final String description;
	
	private DiabetesChronicComplications(String description) {
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