/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * Adds a method to the class to create a detailed and "pretty" formatted description of the instance.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface PrettyPrintable {
	/**
	 * Creates a detailed and "pretty" formatted description of the instance
	 * @param linePrefix A string to prefix each line. This is intended to add tabs to indent inner structures
	 * @return a detailed and "pretty" formatted description of the instance
	 */
	public String prettyPrint(String linePrefix);
}
