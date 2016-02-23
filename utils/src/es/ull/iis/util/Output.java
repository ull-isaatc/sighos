/**
 * 
 */
package es.ull.iis.util;

import java.io.*;

/**
 * Handles the debug messages of the simulator. There are two types of messages: <strong>DEBUG</strong>
 * and <strong>ERROR</strong>. ERROR messages are always shown, but DEBUG messages are showed only if the 
 * <code>debugOn</code> parameter is true.
 * @author Iván Castilla Rodríguez
 *
 */
public class Output {
	/** Normal debug stream. */
	protected OutputStreamWriter out;
	/** Error stream. */
	protected OutputStreamWriter err;
	/** Debug activated? */
	protected boolean debugEnabled;
	
	/**
	 * Creates a default output which doesn't print messages.
	 */
	public Output() {
		this(false, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level. 
	 * @param debugOn Prints debug messages if true.
	 */
	public Output(boolean debugOn) {
		this(debugOn, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level and normal debug stream.
	 * @param debugOn Prints debug messages if true.
	 * @param out Normal debug stream.
	 */
	public Output(boolean debugOn, OutputStreamWriter out) {
		this(debugOn, out, new OutputStreamWriter(System.err));
	}
	
	/**
	 * Creates an output with the specified level, normal debug stream and error stream.
	 * @param debugOn Prints debug messages if true.
	 * @param out Normal debug stream.
	 * @param err Error stream.
	 */
	public Output(boolean debugOn, OutputStreamWriter out, OutputStreamWriter err) {
		this.out = out;
		this.err = err;
		this.debugEnabled = debugOn;
	}
	
	/**
	 * Prints an error message.
	 * @param description A text which describes the error.
	 */
	public void error(String description) {
		try {
			err.write("#" + Thread.currentThread().getName() + "#\tERROR!\t" + description + "\r\n");
			err.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Prints a message to the output and terminates the current line by writing 
	 * the line separator string. 
	 * @param msg Description of the message.
	 */
	public void debug(String msg) {
		if (debugEnabled) {
			try {
				out.write("#" + Thread.currentThread().getName() + "#\t" + msg + "\r\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
	}

	/**
	 * @return the debugEnabled
	 */
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
}
