/**
 * 
 */
package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.CreatesSecondOrderParameters;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface DefinesBaseUtility extends CreatesSecondOrderParameters, Named {
	/**
	 * Returns the base utility for the specified patient 
	 * @param pat A patient
	 * @return the base utility for the specified patient
	 */
	public default double getBaseUtility(Patient pat) {
		return UtilityParamDescriptions.BASE_UTILITY.getValue(getRepository(), this, pat.getSimulation());
	}
}
