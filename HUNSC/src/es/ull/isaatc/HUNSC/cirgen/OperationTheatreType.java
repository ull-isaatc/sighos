/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public enum OperationTheatreType {
	OR ("No ambulante"),
	DC ("Ambulante");
	
	private final String name;
	private OperationTheatreType(String name) {
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
