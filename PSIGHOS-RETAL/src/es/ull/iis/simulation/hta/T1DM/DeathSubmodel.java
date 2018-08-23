/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DeathSubmodel {

	/**
	 * 
	 */
	public DeathSubmodel() {
	}

	public abstract long getTimeToDeath(T1DMPatient pat);
}
