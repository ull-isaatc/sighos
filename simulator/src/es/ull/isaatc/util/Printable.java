/**
 * 
 */
package es.ull.isaatc.util;

/**
 * Indicates that this object can print a message in the Output.
 * @author Iván Castilla Rodríguez
 */
public interface Printable {
	/**
	 * Prints the specified message.
	 * @param msg Description of the message.
	 */
	void print(String msg);
	/**
	 * Prints the specified error message.
	 * @param description Text describing the error.
	 */
	void printError(String description);
}
