package es.ull.iis.simulation.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

/**
 * Manage the creation of ByteArrayJFOs. ByteJavaFileManager is a subclass 
 * of ForwardingJavaFileManager, which allows it to forward tasks to a 
 * given file manager.
 * @author ycallero
 *
 * @param <M> Normally, a Standard Java File Manager
 */
public class ByteJavaFileManager<M extends JavaFileManager> extends ForwardingJavaFileManager<M> {

	/** maps class names to JFOs containing the classes' byte codes */
	private Map<String, JavaFileObject> store = new HashMap<String, JavaFileObject>();

	/**
	 * Create a new ByteJavaFileManager.
	 * @param fileManager Normally, a Standard Java File Manager.
	 * @param str Store.
	 */
	public ByteJavaFileManager(M fileManager, Map<String, JavaFileObject> str) {
		super(fileManager);
		store = str;
	}
	
	/**
	 * ByteJavaFileManager overrides 
	 * ForwardingJavaFileManager.getJavaFileForOutput(), which is called 
	 * by the compiler to create a new Java file object for holding its 
	 * output.
	 * @param location A location.
	 * @param className Class name.
	 * @param kind The kind of file, must be one of SOURCE or CLASS.
	 * @param     sibling A file object to be used as hint for placement; might be null. 
	 */
	public JavaFileObject getJavaFileForOutput(Location location,
			String className, Kind kind, FileObject sibling) throws IOException {
		try {
			JavaFileObject jfo = new ByteArrayJFO(className, kind);
			store.put(className, jfo);
			return jfo;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}
