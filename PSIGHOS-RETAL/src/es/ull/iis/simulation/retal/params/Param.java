/**
 * 
 */
package es.ull.iis.simulation.retal.params;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class Param {
	/** True if first order parameters must be generated; false if second order analysis must be performed */
	protected final boolean baseCase;

	/**
	 * 
	 */
	public Param(boolean baseCase) {
		this.baseCase = baseCase;
	}

}
