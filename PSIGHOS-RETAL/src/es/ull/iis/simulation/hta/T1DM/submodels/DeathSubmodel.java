/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.submodels;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iv�n Castilla Rodr�guez
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
