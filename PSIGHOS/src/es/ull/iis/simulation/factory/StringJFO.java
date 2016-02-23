package es.ull.iis.simulation.factory;
import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Java File Object used to store the java code. We define it because it
 * can make an easier comunication with the ByteJavaFileManager.
 * @author ycallero
 *
 */
public class StringJFO extends SimpleJavaFileObject {
	
	/** Java code. */
	private String codeStr = null;
	/** New class name */
	private String className = null;

	/**
	 * Create a new StringJFO.
	 * @param className Code's identifier.
	 * @param codeStr Java code.
	 * @throws Exception 
	 */
	public StringJFO(String className, String codeStr) throws Exception {
		super(new URI(className + ".java"), Kind.SOURCE); // store source code
		this.className = className;
		this.codeStr = codeStr;
	}

	/**
	 * Called by the Java compiler internally to get the source code.
	 */
	public CharSequence getCharContent(boolean errs) throws IOException {
		return codeStr;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
}
