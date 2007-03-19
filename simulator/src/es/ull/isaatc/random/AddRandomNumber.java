/**
 * 
 */
package es.ull.isaatc.random;

/**
 * Represents the addition of several random distributions.
 * @author Iván Castilla Rodríguez
 *
 */
public class AddRandomNumber extends CompoundRandomNumber {
	
	/**
	 * 
	 */
	public AddRandomNumber(RandomNumber r1, RandomNumber r2) {
		super(r1, r2);
	}

	protected double operateDouble(double sample1, double sample2) {
		return sample1 + sample2;
	}

	protected int operateInt(int sample1, int sample2) {
		return sample1 + sample2;
	}     
}
