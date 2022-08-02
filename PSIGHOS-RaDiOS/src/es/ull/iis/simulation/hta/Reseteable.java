/**
 * 
 */
package es.ull.iis.simulation.hta;

/**
 * Indicates that the class contains components that must be restarted among the simulation of every intervention.
 * @author Iván Castilla Rodríguez
 *
 */
public interface Reseteable {
	/**
	 * Restarts the class components among interventions. Useful to reuse already computed values for a previous intervention and
	 * preserve common random numbers
	 * @param id Identifier of the simulation to reset
	 */
	void reset(int id);
}
