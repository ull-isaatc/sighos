/**
 * 
 */
package es.ull.isaatc.function;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.random.RandomNumber;


/**
 * Represents the linear function: A·x + B.
 * @author Iván Castilla Rodríguez
 *
 */
public class LinearFunction implements TimeFunction {
	private RandomNumber a;
	private RandomNumber b;
	
	/**
	 * 
	 */
	public LinearFunction(RandomNumber a, RandomNumber b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * 
	 */
	public LinearFunction(double a, double b) {
		this.a = new Fixed(a);
		this.b = new Fixed(b);
	}

	public double getValue(double ts) {
		return a.sampleDouble() * ts + b.sampleDouble();
	}
}
