/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * A submodel for progression to death.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DeathSubmodel {

	/**
	 * Creates a submodel for death
	 */
	public DeathSubmodel() {
	}

	/**
	 * Returns the time to death of the specified patient
	 * @param pat A patient
	 * @return the time to death of the specified patient
	 */
	public abstract long getTimeToDeath(T1DMPatient pat);
}
