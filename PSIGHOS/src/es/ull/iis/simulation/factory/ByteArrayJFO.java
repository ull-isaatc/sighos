package es.ull.iis.simulation.factory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * Creates a Java file object which accepts byte codes on its input 
 * stream, and sends them to its output stream. It also allows the 
 * output stream to be accessed as a byte array.
 * @author ycallero
 *
 */
public class ByteArrayJFO extends SimpleJavaFileObject {

	/** Output Stream */
	private ByteArrayOutputStream baos = null;

	/**
	 * Create a new ByteArrayJFO.
	 * @param className Class name.
	 * @param kind kind
	 * @throws Exception
	 */
	public ByteArrayJFO(String className, Kind kind) throws Exception {
		super(new URI(className), kind);
	}

	/**
	 * The input stream to the java file object accepts bytes.
	 */
	public InputStream openInputStream() throws IOException	{
		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * The output stream supplies bytes.
	 */
	public OutputStream openOutputStream() throws IOException	{
		return baos = new ByteArrayOutputStream();
	}

	/**
	 * Access the byte output stream as an array
	 * @return A ByteArray.
	 */
	public byte[] getByteArray() {
		return baos.toByteArray();
	}
}
