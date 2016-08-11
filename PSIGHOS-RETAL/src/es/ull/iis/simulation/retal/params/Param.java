/**
 * 
 */
package es.ull.iis.simulation.retal.params;

import es.ull.iis.simulation.retal.RETALSimulation;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class Param {
	/** True if first order parameters must be generated; false if second order analysis must be performed */
	protected final boolean baseCase;
	/** The simulation this parameter are attached to */
	protected final RETALSimulation simul;

	/**
	 * 
	 */
	public Param(RETALSimulation simul, boolean baseCase) {
		this.baseCase = baseCase;
		this.simul = simul;
	}

}
