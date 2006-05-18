/**
 * 
 */
package es.ull.cyc.random;

/**
 * Represents the multiplication of several random distributions.
 * @author Iván Castilla Rodríguez
 *
 */
public class MultRandomNumber extends CompoundRandomNumber {
	
	/**
	 * 
	 */
	public MultRandomNumber(RandomNumber r1, RandomNumber r2) {
		super(r1, r2);
	}

	protected double operateDouble(double sample1, double sample2) {
		return sample1 * sample2;
	}

	protected int operateInt(int sample1, int sample2) {
		return sample1 * sample2;
	}     
}
