/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

/**
 * Any intervention that can be performed in a patient
 * @author Iván Castilla Rodríguez
 *
 */
public interface Intervention {

	public String getShortName();
	public String getDescription();
	public int getId();
}
