/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

/**
 * Several methods useful for testing
 * @author Iván Castilla Rodríguez
 *
 */
public interface TestUtils {
	static short DEF_PRECISION = 5; 
	static boolean checkDouble(double reference, double comp, int precision) {
		return String.format("%." + precision + "g%n", comp).equals(String.format("%." + precision + "g%n", reference));
	}
	static boolean checkDouble(double reference, double comp) {
		return checkDouble(reference, comp, DEF_PRECISION);
	}
	static void printCheckedMessage(String message) {
		System.out.println("CHECKED!\t" + message);
	}
	static void printErrorMessage(String message) {
		System.err.println("FAILED! \t" + message);
	}
}
