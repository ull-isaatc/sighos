/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

/**
 * @author Iván
 *
 */
public enum AdmissionType {
	PROGRAMMED ("Programado"),
	EMERGENCY ("Urgencia");
	
	private final String name;
	private AdmissionType(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
