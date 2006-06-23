/**
 * 
 */
package es.ull.cyc.util;

import java.io.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Output {
	public enum DebugLevel {NODEBUG, DEBUG, XDEBUG};
	// Message types
	public enum MessageType {ERROR, DEBUG};
	protected OutputStreamWriter out;
	protected OutputStreamWriter err;
	protected DebugLevel level;
	
	public Output() {
		this(DebugLevel.DEBUG, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	public Output(DebugLevel level) {
		this(level, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	public Output(DebugLevel level, OutputStreamWriter out) {
		this(level, out, new OutputStreamWriter(System.err));
	}
	
	public Output(DebugLevel level, OutputStreamWriter out, OutputStreamWriter err) {
		this.out = out;
		this.err = err;
		this.level = level;
	}
	
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
	
	public void print(MessageType type, String description) {
		print(type, description, description);
	}
	
	private static void println(OutputStreamWriter out, String msg) {
		try {
			out.write(msg + "\r\n");
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
