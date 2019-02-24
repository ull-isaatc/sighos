/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * Any intervention that can be performed in a patient
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface Intervention {

	public String getShortName();
	public String getDescription();
	public int getId();
}
