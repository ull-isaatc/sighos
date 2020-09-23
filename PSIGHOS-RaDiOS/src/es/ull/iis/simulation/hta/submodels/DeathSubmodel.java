/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import es.ull.iis.simulation.hta.Patient;

/**
 * A submodel for progression to death.
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DeathSubmodel extends ComplicationSubmodel {
	protected final SecondOrderDeathSubmodel secOrder;
	/**
	 * Creates a submodel for death
	 */
	public DeathSubmodel(SecondOrderDeathSubmodel secOrder) {
		super();
		this.secOrder = secOrder;
	}

	/**
	 * Returns the time to death of the specified patient
	 * @param pat A patient
	 * @return the time to death of the specified patient
	 */
	public abstract long getTimeToDeath(Patient pat);
}
