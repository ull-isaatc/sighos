/**
 * 
 */
package es.ull.isaatc.util;

/**
 * @author Iván
 *
 */
public class ExtendedMath {
	public static double round(double value, double factor) {
		return Math.round(value / factor) * factor;		
	}

	public static double ceil(double value, double factor) {
		return Math.ceil(value / factor) * factor;		
	}

	public static double floor(double value, double factor) {
		return Math.floor(value / factor) * factor;		
	}
}
