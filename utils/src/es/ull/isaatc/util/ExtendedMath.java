/**
 * 
 */
package es.ull.isaatc.util;

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
}
