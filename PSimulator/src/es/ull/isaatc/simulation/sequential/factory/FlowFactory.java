package es.ull.isaatc.simulation.sequential.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import es.ull.isaatc.simulation.sequential.Element;
import es.ull.isaatc.simulation.sequential.Simulation;
import es.ull.isaatc.simulation.sequential.condition.Condition;
import es.ull.isaatc.simulation.sequential.flow.BasicFlow;
import es.ull.isaatc.simulation.sequential.flow.Flow;

/**
 * Generate Flow's instances.
 * @author ycallero
 *
 */
@SuppressWarnings("unchecked")
public class FlowFactory extends StandardCompilator {

	/** The actual class name */
	static String actualClassName = null;
	static int id = 0;
	/**
     * A list of packages to search for RandomVariates if the
     * class name given is not fully qualified.
     **/
    protected static Set<String> searchPackages;
    /**
     * Holds a cache of the RandomVariate Classes that have already been
     * found indexed by their name.
     **/
    protected static Map<String, Class> cache;
	
    /**
     * If true, print out information while searching for RandomVariate
     * Classes.
     **/
    protected static boolean verbose;
    
	/**
	 * Create de Flow's classes index.
	 */
	static {
		searchPackages = new LinkedHashSet<String>();
        searchPackages.add("es.ull.isaatc.simulation.flow");
        cache = new WeakHashMap<String, Class>();
	}
	
	/**
	 * Finds the Flow Class corresponding to the given name. First
	 * attempts to find the Flow assuming the the name is fully qualified.
	 * Then searches the "search packages." The search path defaults to "es.ull.isaatc.flow"
	 * but additional search packages can be added.
	 * @see #addSearchPackage(String)
	 * @see #setSearchPackages(Set)
	 **/
	public static Class findFullyQualifiedNameFor(String className) {
		Class theClass = null;
		
        // First check cache
        theClass = cache.get(className);
        if (theClass != null)
        	return theClass;
        
        // If not, see if name passed is "fully qualified"
		try {
			theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
			cache.put(className, theClass);
			return theClass;
		}
		//        If not, then try the search path
		catch (ClassNotFoundException e) {}
		for (String searchPackage : searchPackages) {
			if (verbose) {
				System.out.println("Checking " + searchPackage + "." + className);
			}
			try {
				theClass = Thread.currentThread().getContextClassLoader().loadClass(
						searchPackage + "." + className );
				if (!es.ull.isaatc.simulation.model.flow.Flow.class.isAssignableFrom(theClass)) {
					continue;
				}
			} catch (ClassNotFoundException e) { continue; }
		}
		if (verbose) {System.out.println("returning " + theClass);}
		if (theClass != null)
			cache.put(className, theClass);
		return theClass;
	}
	
	/**
	 * Generate a new Flow with events code. The events that you can 
	 * include are:
	 *    - Imports indexed by "imports".
	 *    - Before request event indexed by "beforeRequest".
	 *    - Inqueue event indexed by "inqueue". Accion activated when an 
	 *      activity have to wait for resources which can performed it.
	 *      Only in SingleFlows. 
	 *    - After start event indexed by "afterStart". Accion activated 
	 *      when an activity obtain the resources and is prepared to start
	 *      the task.
	 *    - When the activity is performed event indexed by "afterFinalize".
	 *    
	 * @param id Identifier.
	 * @param code Events code.
	 * @param packageName Package name of the Flow.
	 * @param metaFlowType The specific Flow. 
	 * @param cl Class of the speciofic Flow.
	 * @return The class code including the events.
	 */
	static private String generateClass(Integer id, CodeContainer code, String packageName, String metaFlowType, Class<?> cl) {
		
		boolean starter = false;
		boolean finish = false;
		
		if (cl == null)
			return null;
		
		// Some Flows can't implement beforeRequest event.
		try {
			cl.getMethod("beforeRequest", Element.class);
			starter = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {}
		
		// Some Flows can't implement afterFinalize event.
		try {
			cl.getMethod("afterFinalize", Element.class);
			finish = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {}
				
		String finalCode = new String();
		
		// Package
		finalCode += "package " + packageName + ";";
		
		// Imports
		finalCode += "import " + Condition.class.getPackage().toString().substring(8) + ".*;";
		finalCode += "import " + Flow.class.getPackage().toString().substring(8) + ".*;";
		String temp = code.get("imports");
		if (temp != null) {
			finalCode += temp;
			temp = null;
		}
		
		// Class denifition
		finalCode += "public class Compiled" + metaFlowType + id + " extends " + metaFlowType + "{";
		
		// Constructor
		if (metaFlowType.equals("SingleFlow")) {
			finalCode += "public Compiled" + metaFlowType + id + "(Simulation sim, TimeDrivenActivity act){";
			finalCode += "super(sim, act);";
		} else {
			finalCode += "public Compiled" + metaFlowType + id + "(Simulation sim){";
			finalCode += "super(sim);";
		}
		finalCode += "}";
		
		if (starter) {
			// BeforeRequest method
			temp = code.get("beforeRequest");
			if (temp != null) {
				finalCode += "public boolean beforeRequest(Element e) {";
				finalCode += getCode(temp, "beforeRequest");
				finalCode += "}";
				temp = null;
			}
		}

		if (metaFlowType.equals("SingleFlow")) {
			// Inqueue method
			temp = code.get("inqueue");
			if (temp != null) {
				finalCode += "public void inqueue(Element e) {";
				finalCode += getCode(temp, "inqueue");
				finalCode += "}";
				temp = null;
			}
			
			// AfterStart method
			temp = code.get("afterStart");
			if (temp != null) {
				finalCode += "public void afterStart(Element e) {";
				finalCode += getCode(temp,"afterStart");
				finalCode += "}";
				temp = null;
			}
		}
		
		if (finish) {
			// AfterFinalize method
			temp = code.get("afterFinalize");
			if (temp != null) {
				finalCode += "public void afterFinalize(Element e) {";
				finalCode += getCode(temp, "afterFinalize");
				finalCode += "}";
			}
		}
		
		// Class close
		finalCode += "}";
		
		
		return finalCode;
	}
	
	static private String generateClass(es.ull.isaatc.simulation.model.flow.Flow f) {
		// Package
		String finalCode = "package es.ull.isaatc.simulation.sequential.flow;";

		// Imports
		finalCode += "import es.ull.isaatc.simulation.sequential.condition.*;";
		String temp = f.getImports();
		if (!temp.isEmpty())
			finalCode += temp;
		
		boolean starter = false;
		boolean finish = false;
		
		// Some Flows can't implement beforeRequest event.
		try {
			cl.getMethod("beforeRequest", Element.class);
			starter = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {}
		
		// Some Flows can't implement afterFinalize event.
		try {
			cl.getMethod("afterFinalize", Element.class);
			finish = true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {}
				
		
		// Class denifition
		finalCode += "public class Compiled" + f.getClass().getCanonicalName() + id + " extends " + metaFlowType + "{";
		
		// Constructor
		if (metaFlowType.equals("SingleFlow")) {
			finalCode += "public Compiled" + metaFlowType + id + "(Simulation sim, TimeDrivenActivity act){";
			finalCode += "super(sim, act);";
		} else {
			finalCode += "public Compiled" + metaFlowType + id + "(Simulation sim){";
			finalCode += "super(sim);";
		}
		finalCode += "}";
		
		if (starter) {
			// BeforeRequest method
			temp = code.get("beforeRequest");
			if (temp != null) {
				finalCode += "public boolean beforeRequest(Element e) {";
				finalCode += getCode(temp, "beforeRequest");
				finalCode += "}";
				temp = null;
			}
		}

		if (metaFlowType.equals("SingleFlow")) {
			// Inqueue method
			temp = code.get("inqueue");
			if (temp != null) {
				finalCode += "public void inqueue(Element e) {";
				finalCode += getCode(temp, "inqueue");
				finalCode += "}";
				temp = null;
			}
			
			// AfterStart method
			temp = code.get("afterStart");
			if (temp != null) {
				finalCode += "public void afterStart(Element e) {";
				finalCode += getCode(temp,"afterStart");
				finalCode += "}";
				temp = null;
			}
		}
		
		if (finish) {
			// AfterFinalize method
			temp = code.get("afterFinalize");
			if (temp != null) {
				finalCode += "public void afterFinalize(Element e) {";
				finalCode += getCode(temp, "afterFinalize");
				finalCode += "}";
			}
		}
		
		// Class close
		finalCode += "}";
		
		
		return finalCode;
	}
	
	/**
	 * Generate a StringJFO from the Object's identifier and the
	 * code of the compiled class.
	 * 
	 * @param id Identifier.
	 * @param code Events code.
	 * @param flowType Flow's type.
	 * @return A StringJFO with the class code and the class name.
	 */
	static private StringJFO makeCode(es.ull.isaatc.simulation.model.flow.Flow f) {		
		
		String classCode = generateClass(id, code, "es.ull.isaatc.simulation", flowType, findFullyQualifiedNameFor(flowType));
		StringJFO src = null;
		try {
			actualClassName = new String("Compiled" + flowType + id);
			src = new StringJFO(actualClassName + ".java", classCode);
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
	 * @param flowType Flow's type.
	 * @return An array of constructors.
	 */
	static private Constructor<?>[] getConstructor(es.ull.isaatc.simulation.model.flow.Flow f){
		
		try {
			Class<?> cl;
			if ((code != null) && (code.size() != 0)) {
				StringJFO src = makeCode(id, code, flowType);
				id++;
				ByteClassLoader loader = new ByteClassLoader(store);
				if (loader.isCompiledClass("es.ull.isaatc.simulation." + actualClassName))
					store.remove("es.ull.isaatc.simulation." + actualClassName);
				compileCode(src);					
				cl = loader.loadClass("es.ull.isaatc.simulation." + actualClassName);
			} else 
				cl = findFullyQualifiedNameFor(flowType);
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
	 * Get a Flow's instance.
	 * @param objectType Flow's type.
	 * @param id Identifier.
	 * @param sim Actual simulation.
	 * @param params Rest of the params.
	 * @return A Flow's instance.
	 */
	static public Flow getInstance(Simulation sim, es.ull.isaatc.simulation.model.flow.Flow f) {
		
		Constructor<?>[] cons;
		// Prepare the new params.
		Object[] newParams;
		// Obtain the Class's constructors.
		if ((params.length != 0) && (params[0] instanceof CodeContainer)) {
			cons = getConstructor((CodeContainer) params[0], objectType);
			newParams = params;
		} else {
			cons = getConstructor(null, objectType);
			newParams = new Object[params.length+1];
			for (int i = 0; i < params.length; i++)
				newParams[i+1] = params[i];
		}
		newParams[0] = sim;
		// Try to obtain a new instance.
		for (int i = 0; i < cons.length; i++) {
			try {
				BasicFlow meta = (BasicFlow) cons[i].newInstance(newParams);
				return meta;
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
		return null;	
	}

}
