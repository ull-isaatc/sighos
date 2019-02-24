/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

/**
 * A type of parameter that can be reset among simulations. Resetting the parameter does not mean losing the values generated but
 * starting from the first one again.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ReseteableParam<T> extends Param<T> {
	void reset();
}
