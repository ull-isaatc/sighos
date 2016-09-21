/**
 * 
 */
package es.ull.iis.simulation.retal.params;

/**
 * @author Iván Castilla
 *
 */
public enum OphthalmologicResource {
	OPH_APPOINTMENT("Outpatient ophthalmologist appointment", 149.8),	// Source: Spanish Official Tariffs
	APPOINTMENT("Outpatient appointment", 62.43),	// Source: Spanish Official Tariffs
	OCTAL("Use of software", 0.0),					// Source: Assumption
	OCT("OCT", 182.29),								// Source: Spanish Official Tariffs
	CLINICAL_EXAM("Clinical Examination", 149.8),	// Source: Spanish Official Tariffs, same as out. appointment
	RETINO("Retinography", 99.5),					// Source: Spanish Official Tariffs
	ANGIO("Angiography", 173.88),					// Source: Spanish Official Tariffs
	RANIBIZUMAB("Ranibizumab", 913.1),				// Source: Portalfarma
	PHOTOCOAGULATION("Photocoagulation", 982.96),	// Source: Spanish Official Tariffs 
	TEST("Only for test", 100.0)
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
