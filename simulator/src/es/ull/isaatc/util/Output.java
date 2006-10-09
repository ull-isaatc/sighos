/**
 * 
 */
package es.ull.isaatc.util;

import java.io.*;

/**
 * Handles the debug messages of the simulator. There are two types of messages: <strong>DEBUG</strong>
 * and <strong>ERROR</strong>. ERROR messages are always shown, but DEBUG messages are treated by using
 * three levels:
 * <ul>
 * <li><strong>NODEBUG</strong></li>: It doesn't print any message. Only error messages are showed.
 * <li><strong>DEBUG</strong></li>: Print short messages.
 * <li><strong>XDEBUG</strong></li>: Print detailed messages.
 * </ul>
 * @author Iván Castilla Rodríguez
 *
 */
public class Output {
	/** Detail level of debug messages. */
	public enum DebugLevel {NODEBUG, DEBUG, XDEBUG};
	/** Message types. */
	public enum MessageType {ERROR, DEBUG};
	/** Normal debug stream. */
	protected OutputStreamWriter out;
	/** Error stream. */
	protected OutputStreamWriter err;
	/** Detail level used for debug messages. */
	protected DebugLevel level;
	
	/**
	 * Creates a default output which doesn't print messages.
	 */
	public Output() {
		this(DebugLevel.NODEBUG, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level. 
	 * @param level Detail level of debug messages.
	 */
	public Output(DebugLevel level) {
		this(level, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level and normal debug stream.
	 * @param level Detail level of debug messages.
	 * @param out Normal debug stream.
	 */
	public Output(DebugLevel level, OutputStreamWriter out) {
		this(level, out, new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level, normal debug stream and error stream.
	 * @param level Detail level of debug messages.
	 * @param out Normal debug stream.
	 * @param err Error stream.
	 */
	public Output(DebugLevel level, OutputStreamWriter out, OutputStreamWriter err) {
		this.out = out;
		this.err = err;
		this.level = level;
	}
	
	/**
	 * Prints the specified message.
	 * @param type Message type.
	 * @param shortDescription Short description of the message.
	 * @param longDescription Long description of the message.
	 */
	public void print(MessageType type, String shortDescription, String longDescription) {
		if (type == MessageType.ERROR) {
			if (level == DebugLevel.XDEBUG)
				println(err, longDescription);
			else // Errors and warnings are always showed
				println(err, shortDescription);
		}
		else {
			if (level == DebugLevel.XDEBUG)
				println(out, longDescription);
			else if (level == DebugLevel.DEBUG)
				println(out, shortDescription);
			// If (level == NODEBUGLEVEL) no message is showed 
		}		
	}
	
	/**
	 * Prints the specified message, but uses the same description for short and long
	 * messages.
	 * @param type Message type.
	 * @param description Description of the message.
	 */
	public void print(MessageType type, String description) {
		print(type, description, description);
	}

	/**
	 * Prints a message to the specified output and terminates the current line by writing 
	 * the line separator string. 
	 * @param out Stream where the messages are printed.
	 * @param msg Message to be printed.
	 */
	private static void println(OutputStreamWriter out, String msg) {
		try {
			out.write(msg + "\r\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
