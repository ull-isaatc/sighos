/**
 * 
 */
package es.ull.isaatc.util;

import es.ull.isaatc.random.RandomNumber;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class RandomNumberGenerator extends NumberGenerator {
	private RandomNumber rnd;
	
	/**
	 * 
	 */
	public RandomNumberGenerator(RandomNumber rnd) {
		super();
		this.rnd = rnd;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.NumberGenerator#getNumber(double)
	 */
	public double getNumber(double ts) {
		return rnd.sampleDouble();
	}

}
