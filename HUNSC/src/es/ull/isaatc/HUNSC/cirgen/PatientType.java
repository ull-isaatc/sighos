/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public enum PatientType {
	OR ("No ambulante"),
	DC ("Ambulante");
	
	private final String name;
	private PatientType(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
