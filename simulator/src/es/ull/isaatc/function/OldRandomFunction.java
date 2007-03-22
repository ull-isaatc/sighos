/**
 * 
 */
package es.ull.isaatc.function;

import es.ull.isaatc.random.RandomNumber;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class OldRandomFunction extends TimeFunction {
	private RandomNumber rnd;
	
	/**
	 * 
	 */
	public OldRandomFunction(RandomNumber rnd) {
		super();
		this.rnd = rnd;
	}

	/**
	 * @return Returns the rnd.
	 */
	public RandomNumber getRandom() {
		return rnd;
	}

	/**
	 * @param rnd The rnd to set.
	 */
	public void setRandom(RandomNumber rnd) {
		this.rnd = rnd;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.function.TimeFunction#getValue(double)
	 */
	public double getValue(double ts) {
		return rnd.sampleDouble();
	}

}
