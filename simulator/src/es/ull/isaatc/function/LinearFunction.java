/**
 * 
 */
package es.ull.isaatc.function;


/**
 * Represents the linear function: A·x + B.
 * @author Iván Castilla Rodríguez
 *
 */
public class LinearFunction extends TimeFunction {
	private TimeFunction a;
	private TimeFunction b;
	
	/**
	 * 
	 */
	public LinearFunction(TimeFunction a, TimeFunction b) {
		this.a = a;
		this.b = b;
	}
	
	/**
	 * 
	 */
	public LinearFunction(double a, double b) {
		this.a = new ConstantFunction(a);
		this.b = new ConstantFunction(b);
	}

	/**
	 * @return Returns the a.
	 */
	public TimeFunction getA() {
		return a;
	}

	/**
	 * @return Returns the b.
	 */
	public TimeFunction getB() {
		return b;
	}

	/**
	 * @param a The a to set.
	 */
	public void setA(TimeFunction a) {
		this.a = a;
	}

	/**
	 * @param b The b to set.
	 */
	public void setB(TimeFunction b) {
		this.b = b;
	}

	public double getValue(double ts) {
		return a.getValue(ts) * ts + b.getValue(ts);
	}
}
