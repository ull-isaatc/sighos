package es.ull.iis.simulation.factory;
import java.util.Map;

import javax.tools.JavaFileObject;

/**
 * Class Loader used to load Byte Codes.
 * @author ycallero
 *
 */
public class ByteClassLoader extends ClassLoader{
	
	/** Global store */
	private Map<String, JavaFileObject> store;

	/** 
	 * Create a new ByteClassLoader.
	 * @param str Byte Code's Store.
	 */
	public ByteClassLoader(Map<String, JavaFileObject> str) {
		super(ByteClassLoader.class.getClassLoader()); // set parent
		store = str;
	}
	
	/**
	 * The overridden findClass() finds byte codes by looking for the named 
	 * class in the HashMap, and extracting them from the associated file 
	 * object. The byte codes are passed to the JVM via 
	 * ClassLoader.defineClass().
	 * @param name Class name.
	 */
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		JavaFileObject jfo = store.get(name); // load java file object
		if (jfo == null)
			throw new ClassNotFoundException(name);
		byte[] bytes = ((ByteArrayJFO) jfo).getByteArray();
		// get byte codes array
		Class<?> cl = defineClass(name, bytes, 0, bytes.length);
		// send byte codes to the JVM
		if (cl == null)
			throw new ClassNotFoundException(name);
		return cl;
	}
	
	/**
	 * Check if a class is already compiled.
	 * @param name Class name.
	 * @return True if the class was compiled.
	 */
	public boolean isCompiledClass(String name) {
		JavaFileObject jfo = store.get(name); // load java file object
		if (jfo == null)
			return false;
		return true;
	}
}
