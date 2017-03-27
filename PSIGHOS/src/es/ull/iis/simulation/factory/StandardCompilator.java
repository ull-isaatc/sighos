package es.ull.iis.simulation.factory;

import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;



/**
 * Class defined to simulate a standar Java compilator. Thas class can 
 * invoke the Java compiler and store the bytecode for futures 
 * instances. 
 * @author ycallero
 *
 */
public class StandardCompilator {
	/** A collector for diagnostics messages */
	private static DiagnosticCollector<JavaFileObject> diagnostics;
	/** A bytecode cache */
	private static Map<String, JavaFileObject> bytecodeCache = new TreeMap<String, JavaFileObject>();
	/** A temporal store used for internals operations */
	private static Map<String, JavaFileObject> tempStore = new TreeMap<String, JavaFileObject>();
	
	/**
	 * @return the store
	 */
	public static Map<String, JavaFileObject> getBytecodeCache() {
		return bytecodeCache;
	}

	/**
	 * Invoke the java compilator and compile the code. Store the bytecode
	 * in the store.
	 * @param src Java code.
	 */
	static public void compileCode(StringJFO src) {
		
		CompilationTask task = makeCompilerTask(src, tempStore);
//		System.out.println("Compiling...");
		boolean hasCompiled = task.call(); // carry out the compilation
		for (Diagnostic<?> d : diagnostics.getDiagnostics())
			System.out.println(d);
		if (!hasCompiled) {
			System.out.println("Compilation failed");
			System.exit(1);
		} //else
//			System.out.println("Generated Classes: " + tempStore.keySet());
		bytecodeCache.putAll(tempStore);
		tempStore.clear();
	}
	
	/**
	 * Create a compilation operation object for compiling the string java file
	 * object, src. Use a specialized Java file manager. The resulting class is
	 * saved in store under the class name.
	 * 
	 * @param src Java code.
	 * @param storeInput Bytecode store.
	 * @return Compilation operation.
	 */
	static private CompilationTask makeCompilerTask(StringJFO src,
			Map<String, JavaFileObject> storeInput) {
		
		// Obtain Java compiler
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			System.out.println("Compiler not found");
			System.exit(1);
		}
		// create a collector for diagnostic messages 
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		// create a standard manager for the Java file objects
		StandardJavaFileManager fileMan = compiler.getStandardFileManager(
				diagnostics, null, null);
		/*
		 * forward most calls to the standard Java file manager but put the
		 * compiled classes into the store with ByteJavaFileManager
		 */
		ByteJavaFileManager<StandardJavaFileManager> jfm = new ByteJavaFileManager<StandardJavaFileManager>(
				fileMan, storeInput);
		/*
		 * create a compilation task using the supplied file manager, diagnostic
		 * collector, and applied to the string Java file object (in a list)
		 */
		return compiler.getTask(null, jfm, diagnostics, null, null, Arrays
				.asList(src));
	}
	
	/**
	 * Process the code looking for access variable's tags. If a tag is 
	 * found it's changed to the java code traduction.
	 * @param code Java code.
	 * @return Parsed code.
	 */
	static public String getCode(String code, String context) {		
		  try {
			  	TagProcessLexer lex = new TagProcessLexer(new StringReader(code));
			  	lex.setContext(context);
			    TagProcessParser p = new TagProcessParser(lex);
			    String temp = (String)p.parse().value; 
			    return temp;
			  }
		  catch (Exception e) {
			  e.printStackTrace();
			  return ("return false;");
		  }
	}

	/**
	 * Generate a new instance of the specified class with events code. 	 * @param id Identifier.
	 * @param code Events code.
	 * @return The Class code including the events.
	 */
	static private String generateClass(String workingPkg, String objectType, Integer id, String constructorStr, SimulationUserCode userMethods) {
						
		String finalCode = new String();
		
		// Package
		finalCode += "package " + workingPkg + ";";
		
		// Imports
		if (userMethods.getImports() != null)
			finalCode += userMethods.getImports();
		
		// Class definition
		finalCode += "public class Compiled" + objectType + id + " extends " + objectType + " {";
		
		// Constructor
		finalCode += "public Compiled" + objectType  + id + constructorStr;
		
		for (UserMethod m : userMethods.getDefinedMethods()) {
			finalCode += m.getMethodHeading() + "{" + getCode(userMethods.get(m), m.getName()) + "}";
		}
		
		// Class close
		finalCode += "}";
		
		return finalCode;
	}
	
	/**
	 * Get the constructors of the class, compiled or not.
	 * @param id Identifier.
	 * @param code Events code.
	 * @return An array of constructors.
	 */
	static private Constructor<?>[] getConstructors(String workingPkg, StringJFO src){
		
		Class<?> cl;
		try {
			ByteClassLoader loader = new ByteClassLoader(bytecodeCache);
			if (loader.isCompiledClass(workingPkg + "." + src.getClassName()))
				bytecodeCache.remove(workingPkg + "." + src.getClassName());
			compileCode(src);
			cl = loader.loadClass(workingPkg + "." + src.getClassName());
			Constructor<?>[] cons = cl.getConstructors();
			return cons;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns an instance of a simulation object.
	 * There are several important remarks:
	 * <ol>
	 * <li>The class code is generated</li>
	 * <li>The class code is packed into an special structure</li>
	 * <li>The class is compiled and the constructors returned</li>
	 * <li>Each constructor is tried until a suitable one is found</li>
	 * </ol>
	 * The reason not to directly look for a specific constructor is that the <code>Class.getConstructor</code> method
	 * only returns a constructor when the match is PERFECT, that is, no subclasses are allowed.
	 * @param workingPkg The working package of the instanced class
	 * @param objectType The simple name of the class
	 * @param id A unique identifier for this instance among the other class instances 
	 * @param constructorStr How the constructor is defined, starting with the parameters, e.g. "(ParallelSimulationEngine simul){super(simul);}" 
	 * @param userMethods User code and imports to be included in the new instance
	 * @param initargs Arguments passed to the constructor when creating the instance 
	 * @return An instance of a simulation object
	 */
	static public Object getInstance(String workingPkg, String objectType, int id, String constructorStr, SimulationUserCode userMethods, Object... initargs) {
		// Generates the class code
		String classCode = generateClass(workingPkg, objectType, id, constructorStr, userMethods);
		// Generates a StringJFO from the Object's identifier and the code of the compiled class
		StringJFO src = null;
		try {
			src = new StringJFO("Compiled" + objectType + id, classCode);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		Constructor<?>[] consList = getConstructors(workingPkg, src);
		for (int i = 0; i < consList.length; i++) {
			try {
				return consList[i].newInstance(initargs);
			} catch (IllegalArgumentException e) {
				if (i == consList.length - 1)
					e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * Returns an array which contains the corresponding classes of the objects passed as parameter.
	 * @param objects A collection of objects
	 * @return An array which contains the corresponding classes of the objects passed as parameter.
	 */
	static public Class<?>[] param2Classes(Object... objects) {
		Class<?>[] temp = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++)
			temp[i] = objects[i].getClass();
		return temp;
	}
}
