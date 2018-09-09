/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

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
