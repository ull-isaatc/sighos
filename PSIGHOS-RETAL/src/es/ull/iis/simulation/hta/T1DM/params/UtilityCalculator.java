/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface UtilityCalculator {
	public enum CombinationMethod {
		ADD,
		MIN,
		MULT
	}

	public double getHypoEventDisutilityValue();
	public double getUtilityValue(T1DMPatient pat);

}
