/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * Generic class for relative risks for complications 
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class ComplicationRR {

	/**
	 * 
	 */
	public ComplicationRR() {
	}

	public abstract double getRR(T1DMPatient pat);
}
