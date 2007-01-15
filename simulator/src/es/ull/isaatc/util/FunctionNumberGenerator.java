/**
 * 
 */
package es.ull.isaatc.util;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class FunctionNumberGenerator extends NumberGenerator {
	private TimeFunction fun;
	
	/**
	 * 
	 */
	public FunctionNumberGenerator(TimeFunction fun) {
		super();
		this.fun = fun;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.NumberGenerator#getNumber(double)
	 */
	public double getNumber(double ts) {
		return fun.getValue(ts);
	}

}
