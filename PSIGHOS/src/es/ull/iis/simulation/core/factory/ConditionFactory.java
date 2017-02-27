package es.ull.iis.simulation.core.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.Element;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.variable.IntVariable;
import es.ull.iis.simulation.variable.Variable;

/**
 * Generate Condition's instances. We can obtain a predefined Condition
 * like PercentageCondition, NotCondition,... Moreover, we can define a 
 * condition through a logic expresion which can use simulation variables.
 * @author ycallero
 *
 */
public class ConditionFactory {
	// TODO: Change to get package automatically
	private final static String workingPkg = "es.ull.iis.simulation.common.condition";
	
	/**
	 * Generate a new condition through a logic expression. Parse the
	 * expression and generate the string with the new Condition's code.  
	 * @param id Identifier.
	 * @param code Container which can house: 
	 *                  - Imports code indexed by "imports".
	 *                  - Logic expression indexed by "logicExp".
	 * @return A string with the new class code.
	 */
	static private String generateClass(Integer id, String imports, String condition) {
		
		String finalCode = new String();

		// Package
		finalCode += "package " + workingPkg + ";";

		// Include imports
		finalCode += "import " + Condition.class.getName() + ";";	
		finalCode += "import " + SimulationEngine.class.getName() + ";";
		finalCode += "import " + Element.class.getName() + ";";
		finalCode += "import " + IntVariable.class.getName() + ";";
		finalCode += "import " + Variable.class.getName() + ";";
		
		if (imports != null)
			finalCode += imports;
		
		// Class denifition
		finalCode += "public class CompiledCondition" + id + " extends Condition {";
		
		// Constructor
		finalCode += "public CompiledCondition" + id + 
					"(Simulation simul) {super(simul);}";
		
		finalCode += "public boolean check(Element e){" + "return(" + StandardCompilator.getCode(condition, "logicExp") + ");" + "}";
		
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
	static private Constructor<?> getConstructor(StringJFO src) {
		try {
			
			Class<?> cl;
			ByteClassLoader loader = new ByteClassLoader(StandardCompilator.getBytecodeCache());
			if (loader.isCompiledClass(workingPkg + "." + src.getClassName()))
				StandardCompilator.getBytecodeCache().remove(workingPkg + "." + src.getClassName());
			StandardCompilator.compileCode(src);
			cl = loader.loadClass(workingPkg + "." + src.getClassName());
			Constructor<?> cons = cl.getConstructor(SimulationEngine.class);
			return cons;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}		
	
	/**
	 * Get a Condition's instance.
	 * @param sim Actual simulation.
	 * @param id Identifier.
	 * @param imports Imports of the Condition code.
	 * @param condition Condition code.
	 * @return A Condition's instance.
	 */
	static public Condition getInstance(SimulationEngine<?> sim, int id, String imports, String condition){
		String classCode = generateClass(id, imports, condition);
		StringJFO src = null;
		try {
			src = new StringJFO("CompiledCondition" + id, classCode);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		
		// Obtain the Class's constructors.
		Constructor<?> cons = getConstructor(src);
		Object[] newParams = new Object[] {sim};
		try {
			return (Condition) cons.newInstance(newParams);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
