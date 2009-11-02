package es.ull.isaatc.simulation.sequential.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import es.ull.isaatc.simulation.model.Model;
import es.ull.isaatc.simulation.model.VariableHandler;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.StandAloneLPSimulation;
import es.ull.isaatc.simulation.sequential.condition.Condition;
import es.ull.isaatc.simulation.sequential.flow.Flow;

/**
 * Factory which generate simulation object's instances. 
 * @author ycallero
 *
 */
public class SimulationFactory extends StandardCompilator{
	
	/**
	 * Finds the Flow Class corresponding to the given name. First
	 * attempts to find the Flow assuming the the name is fully qualified.
	 * Then searches the "search packages." The search path defaults to "es.ull.isaatc.flow"
	 * but additional search packages can be added.
	 * @see #addSearchPackage(String)
	 * @see #setSearchPackages(Set)
	 **/
	@SuppressWarnings("unchecked")
	public static Class findFullyQualifiedNameFor(String className, String searchPackage) {
		Class theClass = null;

		// If not, see if name passed is "fully qualified"
		try {
			theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
			return theClass;
		}
		//        If not, then try the search path
		catch (ClassNotFoundException e) {}
		try {
			theClass = Thread.currentThread().getContextClassLoader().loadClass(
					searchPackage + "." + className );
		} catch (ClassNotFoundException e) {}
		return theClass;
	}
	
	public static Simulation getSimulationInstance(int simId, Model model) {
		Simulation sim = new StandAloneLPSimulation(simId, model);
		for (Integer id : model.getResourceTypeList().keySet())
			getInstance("ResourceType", id, sim, model.getResourceType(id));
		for (Integer id : model.getResourceList().keySet())
			getInstance("Resource", id, sim, model.getResource(id));
		for (Integer id : model.getActivityList().keySet()) {
			es.ull.isaatc.simulation.model.Activity act = model.getActivity(id);
			if (act instanceof es.ull.isaatc.simulation.model.TimeDrivenActivity)
				getInstance("TimeDrivenActivity", id, sim, act);
		}
		for (es.ull.isaatc.simulation.model.flow.Flow f : model.getFlowList().values())
			if (sim.getFlow(f.getIdentifier()) == null) {
				getInstance(sim, f);
			}
		return sim;
	}
	
	public static Condition getInstance(Simulation sim, es.ull.isaatc.simulation.model.condition.Condition cond) {
		return ConditionFactory.getInstance(sim, cond);
	}
	
	public static Flow getInstance(Simulation sim, es.ull.isaatc.simulation.model.flow.Flow f) {
		return FlowFactory.getInstance(sim, f);
	}
	
	/**
	 * Obtain a Simulation Object's instance.
	 * @param objectType Object type.
	 * @param id Identifier.
	 * @param sim Actual simulation.
	 * @param params The object's constructor params. (Not include identifier and simulation).
	 * @return An Object instance.
	 */
	public static Object getInstance(String objectType, int id, Simulation sim, VariableHandler obj) {
		// Obtain the Class's constructors.
		Constructor<?> cons[] = getConstructor(objectType, id, obj);
		// Prepare the new params.
		Object[] newParams = new Object[] {sim, obj};
		// Try to obtain a new instance.
		for (int i=0; i < cons.length; i++) {
			try {		
				return cons[i].newInstance(newParams);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				if (i == cons.length-1)
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
	 * Generate a new instance of the specified class with events code. 	 * @param id Identifier.
	 * @param code Events code.
	 * @return The Class code including the events.
	 */
	static private String generateClass(String objectType, Integer id, VariableHandler obj) {
						
		String finalCode = new String();
		
		// Package
		finalCode += "package es.ull.isaatc.simulation.sequential;";
		
		// Imports
		String temp = obj.getImports();
		if (!temp.isEmpty())
			finalCode += temp;
		
		// Class denifition
		finalCode += "public class Compiled" + objectType + id + " extends " + objectType + " {";
		
		// Constructor
		finalCode += "public Compiled" + objectType  + id + 
					"(Simulation simul, es.ull.isaatc.simulation.model.ModelObject modelObj) {" +
					"super(simul, modelObj);" +
					"}";
		
		for (String m : obj.getMethods()) {
			temp = obj.getBody(m);
			if (!temp.isEmpty()) {
				finalCode += obj.getCompleteMethod(m) + "{";
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
	 * @param obj Events code.
	 * @return A StringJFO with the class code and the class name.
	 */
	static private StringJFO makeCode(String objectType, Integer id, VariableHandler obj) {		
		
		String classCode = generateClass(objectType, id, obj);
		StringJFO src = null;
		try {
			src = new StringJFO("Compiled" + objectType + id, classCode);
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
	static private Constructor<?>[] getConstructor(String objectType, Integer id, VariableHandler obj){
		
		Class<?> cl;
		try {
			StringJFO src = makeCode(objectType, id, obj);
			ByteClassLoader loader = new ByteClassLoader(store);
			if (loader.isCompiledClass("es.ull.isaatc.simulation.sequential." + src.getClassName()))
				store.remove("es.ull.isaatc.simulation.sequential." + src.getClassName());
			compileCode(src);
			cl = loader.loadClass("es.ull.isaatc.simulation.sequential." + src.getClassName());
			Constructor<?>[] cons = cl.getConstructors();
			return cons;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}		
	
	
}
