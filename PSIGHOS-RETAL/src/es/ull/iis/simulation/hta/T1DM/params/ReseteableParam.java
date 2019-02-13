/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

/**
 * A type of parameter that can be reset among simulations. Resetting the parameter does not mean losing the values generated but
 * starting from the first one again.
 * @author Iván Castilla Rodríguez
 *
 */
public interface ReseteableParam<T> extends Param<T> {
	/**
	 * Resets the parameter so it can be reused in a new simulation (probably for a new intervention
	 */
	void reset();
}
