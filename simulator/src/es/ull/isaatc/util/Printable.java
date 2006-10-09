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
	 * @param type Message type.
	 * @param shortMessage Short description of the message.
	 * @param longMessage Long description of the message.
	 */
	void print(Output.MessageType type, String shortMessage, String longMessage);
}
