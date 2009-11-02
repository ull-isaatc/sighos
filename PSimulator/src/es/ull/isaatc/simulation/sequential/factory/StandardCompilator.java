package es.ull.isaatc.simulation.sequential.factory;

import java.io.StringReader;
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
	/** A bytecode store */
	static Map<String, JavaFileObject> store = new TreeMap<String, JavaFileObject>();
	/** A temporal store used for internals operations */
	private static Map<String, JavaFileObject> tempStore;
	
	/**
	 * Initialize the temporal storage.
	 */
	static {
		tempStore  = new TreeMap<String, JavaFileObject>();
	}
	
	/**
	 * Invoke the java compilator and compile the code. Store the bytecode
	 * in the store.
	 * @param src Java code.
	 */
	static public void compileCode(StringJFO src) {
		
		CompilationTask task = makeCompilerTask(src, tempStore);
		System.out.println("Compiling...");
		boolean hasCompiled = task.call(); // carry out the compilation
		for (Diagnostic<?> d : diagnostics.getDiagnostics())
			System.out.println(d);
		if (!hasCompiled) {
			System.out.println("Compilation failed");
			System.exit(1);
		} else
			System.out.println("Generated Classes: " + tempStore.keySet());
		store.putAll(tempStore);
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
}
