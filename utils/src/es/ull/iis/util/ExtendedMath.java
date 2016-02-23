/**
 * 
 */
package es.ull.iis.util;

/**
 * A simple class which provides classes to perform some mathematical operations. 
 * @author Iván Castilla Rodríguez
 */
public class ExtendedMath {
	/**
	 * Returns the closest multiple of factor to the argument.
	 * @param value A double value to be rounded to the closest multiple of factor.
	 * @param factor Value is rounded to the closest multiple of factor.
	 * @return The closest multiple of factor to the argument
	 */
	public static double round(double value, double factor) {
		return Math.round(value / factor) * factor;		
	}

	/**
	 * Returns the smallest (closest to negative infinity) double value that is greater 
	 * than or equal to the argument and is equal to a multiple of <code>factor</code>. 
	 * @param value A double value
	 * @param factor The computed ceil is a multiple of this <code>factor</code>
	 * @return The smallest (closest to negative infinity) double value that is greater 
	 * than or equal to the argument and is equal to a multiple of <code>factor</code>. 
	 */
	public static double ceil(double value, double factor) {
		return Math.ceil(value / factor) * factor;		
	}

	/**
	 * Returns the largest (closest to positive infinity) double value that is less than 
	 * or equal to the argument and is equal to a multiple of <code>factor</code>
	 * @param value A double value
	 * @param factor The computed floor is a multiple of this <code>factor</code>
	 * @return The largest (closest to positive infinity) double value that is less than 
	 * or equal to the argument and is equal to a multiple of <code>factor</code>
	 */
	public static double floor(double value, double factor) {
		return Math.floor(value / factor) * factor;		
	}

	/**
	 * 
	 * @param k
	 * @return .
	 */
	public static int nextHigherPowerOfTwo(int k) {
		k--;
		for (int i = 1; i < 32; i <<= 1) {
			k = k | k >> i;
		}
		return k + 1;
	}

	/**
	 * 
	 * @param m
	 * @param n
	 * @return .
	 */
	public static int powInt(int m, int n) {
		int bitMask = n;
		int evenPower = m;
		int result;
		if ((bitMask & 1) != 0) {
			result = m;
		} else {
			result = 1;
		}
		bitMask >>>= 1;
		while (bitMask != 0) {
			evenPower *= evenPower;
			if ((bitMask & 1) != 0) {
				result *= evenPower;
			}
			bitMask >>>= 1;
		} // end while
		return result;
	}

	/**
	 * Raise a double to a positive integer power. Fast version of Math.pow.
	 * 
	 * @param x
	 *            number to be taken to a power.
	 * @param n
	 *            power to take x to. 0 <= n <= Integer.MAX_VALUE Negative
	 *            numbers will be treated as unsigned positives.
	 * @return x to the power n
	 */
	public static double power(double x, int n) {
		int bitMask = n;
		double evenPower = x;
		double result;
		if ((bitMask & 1) != 0) {
			result = x;
		} else {
			result = 1;
		}
		bitMask >>>= 1;
		while (bitMask != 0) {
			evenPower *= evenPower;
			if ((bitMask & 1) != 0) {
				result *= evenPower;
			}
			bitMask >>>= 1;
		} // end while
		return result;
	} // end power
}
