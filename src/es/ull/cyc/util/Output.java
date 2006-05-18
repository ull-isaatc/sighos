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
	// Debug level
	public final static int NODEBUG = -1;
	public final static int DEBUGLEVEL = 0;
	public final static int XDEBUGLEVEL = 1;
	// Message types
	public final static int ERRORMSG = -1;
	public final static int WARNINGMSG = 0;
	public final static int DEBUGMSG = 1;
	protected OutputStreamWriter out;
	protected OutputStreamWriter err;
	protected int level;
	
	public Output() {
		this(0, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	public Output(int level) {
		this(level, new OutputStreamWriter(System.out), new OutputStreamWriter(System.err));
	}
	
	public Output(int level, OutputStreamWriter out) {
		this(level, out, new OutputStreamWriter(System.err));
	}
	
	public Output(int level, OutputStreamWriter out, OutputStreamWriter err) {
		this.out = out;
		this.err = err;
		if (level < NODEBUG)
			level = NODEBUG;
		else if (level > XDEBUGLEVEL)
			level = XDEBUGLEVEL;
		this.level = level;
	}
	
	public void print(int type, String shortDescription, String longDescription) {
		if ((type == ERRORMSG) || (type == WARNINGMSG)) {
			if (level == XDEBUGLEVEL)
				println(err, longDescription);
			else // Errors and warnings are always showed
				println(err, shortDescription);
		}
		else {
			if (level == XDEBUGLEVEL)
				println(out, longDescription);
			else if (level == DEBUGLEVEL)
				println(out, shortDescription);
			// If (level == NODEBUGLEVEL) no message is showed 
		}		
	}
	
	public void print(int type, String description) {
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
