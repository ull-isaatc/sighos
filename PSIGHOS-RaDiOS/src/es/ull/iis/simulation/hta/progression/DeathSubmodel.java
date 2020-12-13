/**
 * 
 */
package es.ull.iis.simulation.hta.progression;

import es.ull.iis.simulation.hta.GenerateSecondOrderInstances;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * A submodel for progression to death.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DeathSubmodel implements GenerateSecondOrderInstances {
	protected final SecondOrderParamsRepository secParams;
	/**
	 * Creates a submodel for death
	 */
	public DeathSubmodel(SecondOrderParamsRepository secParams) {
		this.secParams = secParams;
	}

	/**
	 * Returns the time to death of the specified patient
	 * @param pat A patient
	 * @return the time to death of the specified patient
	 */
	public abstract long getTimeToDeath(Patient pat);

	public abstract void addSecondOrderParams();
}
