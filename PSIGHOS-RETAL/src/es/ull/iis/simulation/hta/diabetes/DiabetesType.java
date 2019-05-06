/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

/**
 * Type of diabetes
 * @author Iván Castilla Rodríguez
 *
 */
public enum DiabetesType {
	T1("Type 1 diabetes mellitus"),
	T2("Type 2 diabetes mellitus");
	
	private final String desc;
	
	private DiabetesType(final String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
