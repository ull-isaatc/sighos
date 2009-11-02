package es.ull.isaatc.simulation.sequential.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.TreeMap;

import es.ull.isaatc.simulation.sequential.BasicElement;
import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.condition.NotCondition;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.simulation.variable.IntVariable;
import es.ull.isaatc.simulation.variable.Variable;

/**
 * Generate Condition's instances. We can obtain a predefined Condition
 * like PercentageCondition, NotCondition,... Moreover, we can define a 
 * condition through a logic expresion which can use simulation variables.
 * @author ycallero
 *
 */
public class ConditionFactory extends StandardCompilator {
	/** Condition's type cache. */
	protected static TreeMap<String, Class<?>> cache;
	static int id = 0;
	
	/**
	 * Initialize the condition's type cache.
	 */
	static {
		cache  = new TreeMap<String, Class<?>>();
		cache.put("Condition", Condition.class);
		cache.put("NotCondition", NotCondition.class);
		cache.put("PercentageCondition", PercentageCondition.class);
		cache.put("TrueCondition", TrueCondition.class);
		cache.put("ElementTypeCondition", ElementTypeCondition.class);
	}
	
	/**
	 * Generate a new condition through a logic expression. Parse the
	 * expression and generate the string with the new Condition's code.  
	 * @param id Identifier.
	 * @param code Container which can house: 
	 *                  - Imports code indexed by "imports".
	 *                  - Logic expression indexed by "logicExp".
	 * @return A string with the new class code.
	 */
	static private String generateClass(Integer id, es.ull.isaatc.simulation.model.condition.Condition cond) {
		
		String finalCode = new String();

		// Package
		finalCode += "package es.ull.isaatc.simulation.sequential.condition;";

		// Include imports
		finalCode += "import " + BasicElement.class.getName() + ";";
		finalCode += "import " + Condition.class.getName() + ";";	
		finalCode += "import " + Simulation.class.getName() + ";";
		finalCode += "import " + Element.class.getName() + ";";
		finalCode += "import " + IntVariable.class.getName() + ";";
		finalCode += "import " + Variable.class.getName() + ";";
		
		// Imports
		String temp = cond.getImports();
		if (!temp.isEmpty())
			finalCode += temp;
		
		// Class denifition
		finalCode += "public class CompiledCondition" + id + " extends Condition {";
		
		// Constructor
		finalCode += "public CompiledCondition" + id + 
					"(Simulation simul) {" +
					"super(simul);" +
					"}";
		
		for (String m : cond.getMethods()) {
			temp = cond.getBody(m);
			if (!temp.isEmpty()) {
				finalCode += cond.getCompleteMethod(m) + "{";
				finalCode += getCode(temp, m) + "}";
			}			
		}
		
		// Class close
		finalCode += "}";
		
		return finalCode;
	}
	
	/**
	 * Generate a StringJFO from the Object's identifier and the
	 * code of the compiled class.
	 * @param id Identifier. 
	 * @param code Container with the events code.
	 * @return A StringJFO with the class code and the class name.
	 */
	static private StringJFO makeCode(Integer id, es.ull.isaatc.simulation.model.condition.Condition cond) {		
		
		String classCode = generateClass(id, cond);
		StringJFO src = null;
		try {
			src = new StringJFO("CompiledCondition" + id, classCode);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
		return src;
	}
	
	/**
	 * Get the constructors of the class, compiled or not.
	 * @param id Identifier.
	 * @param code Events code.
	 * @return An array of constructors.
	 */
	static private Constructor<?>[] getConstructor(Integer id, es.ull.isaatc.simulation.model.condition.Condition cond){
		try {
			Class<?> cl;

			StringJFO src = makeCode(id, cond);
			ByteClassLoader loader = new ByteClassLoader(store);
			if (loader.isCompiledClass("es.ull.isaatc.simulation.sequential.condition." + src.getClassName()))
				store.remove("es.ull.isaatc.simulation.sequential.condition." + src.getClassName());
			compileCode(src);
			cl = loader.loadClass("es.ull.isaatc.simulation.sequential.condition." + src.getClassName());
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
	 * Get a Condition's instance.
	 * @param condType The type of the condition.
	 * @param id Identifier.
	 * @param sim Actual simulation.
	 * @param params Rest of the params.
	 * @return A Condition's instance.
	 */
	static public Condition getInstance(Simulation sim, es.ull.isaatc.simulation.model.condition.Condition cond){
		
		// Obtain the Class's constructors.
		id++;
		Constructor<?>[] cons;
		Object[] newParams = null;
		// Prepare the new params.
		if (cond instanceof es.ull.isaatc.simulation.model.condition.ElementTypeCondition)
			return new ElementTypeCondition(((es.ull.isaatc.simulation.model.condition.ElementTypeCondition)cond).getType());
		else if (cond instanceof es.ull.isaatc.simulation.model.condition.NotCondition)
			return new NotCondition(getInstance(sim, ((es.ull.isaatc.simulation.model.condition.NotCondition)cond).getCond()));
		else if (cond instanceof es.ull.isaatc.simulation.model.condition.TrueCondition)
			return new TrueCondition();
		else if (cond instanceof es.ull.isaatc.simulation.model.condition.PercentageCondition)
			return new PercentageCondition(((es.ull.isaatc.simulation.model.condition.PercentageCondition)cond).getPercentage());
		else {
			cons = getConstructor(id, cond);
			newParams = new Object[1];
			newParams[0] = sim;
			// Try to obtain a new instance.
			for (int i = 0; i < cons.length; i++) {
				try {
					return (Condition) cons[i].newInstance(newParams);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					if (i == cons.length -1)
						e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return null;		
	}
}
