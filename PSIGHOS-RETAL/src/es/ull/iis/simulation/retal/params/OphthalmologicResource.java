/**
 * 
 */
package es.ull.iis.simulation.retal.params;

/**
 * @author Iván Castilla
 *
 */
public enum OphthalmologicResource {
	APPOINTMENT("Outpatient appointment", 149.8),	// Source: Spanish Official Tariffs
	OCT("OCT", 125.07),								// Source: eSalud
	CLINICAL_EXAM("Clinical Examination", 28.41),	// Source: Sender et al. 2013
	RETINO("Retinography", 62.95),					// Source: eSalud
	ANGIO("Angiography", 190.71),					// Source: Casaroli-marano et al. 2013
	RANIBIZUMAB("Ranibizumab", 913.1),				// Source: Portalfarma
	PHOTOCOAGULATION("Photocoagulation", 982.96),	// Source: Spanish Official Tariffs 
	;
	
	private final String description;
	private final double unitCost;
	private OphthalmologicResource(String description, double unitCost) {
		this.description = description;
		this.unitCost = unitCost;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the unitCost
	 */
	public double getUnitCost() {
		return unitCost;
	}
}
