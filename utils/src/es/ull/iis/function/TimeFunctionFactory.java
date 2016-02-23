/**
 * 
 */
package es.ull.iis.function;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import simkit.random.RandomVariateFactory;

/**
 * The same functionality of RandomVariateFactory by Arnold Buss, but it searches for TimeFunction
 * classes first, and then for the RandomVariate ones.
 * @author Iván Castilla Rodríguez
 */
public class TimeFunctionFactory {
    
    /**
     * Holds a cache of the RandomVariate Classes that have already been
     * found indexed by their name.
     **/
    protected static Map<String, Class<?>> cache;
    
    /**
     * A list of packages to search for RandomVariates if the
     * class name given is not fully qualified.
     **/
    protected static Set<String> searchPackages;
    
    /**
     * If true, print out information while searching for RandomVariate
     * Classes.
     **/
    protected static boolean verbose;
    
    /**
     * If true, print out information while searching for RandomVariate
     * Classes.
     **/
    public static void setVerbose(boolean b) { verbose = b; }
    /**
     * If true, print out information while searching for RandomVariate
     * Classes.
     **/
    public static boolean isVerbose() { return verbose; }
    
    /**
     * If true, print out information while searching for RandomVariate
     * Classes.
     **/
    public static Map<String, Class<?>> getCache() { return new WeakHashMap<String, Class<?>>(cache); }
    
	static {
        searchPackages = new LinkedHashSet<String>();
        searchPackages.add("es.ull.iis.function");
        cache = new WeakHashMap<String, Class<?>>();
	}
	
    /**
     * This factory Class should never by instantiated.
     **/
    protected TimeFunctionFactory() {
    }
    
    /**
     * Creates a <CODE>TimeFunction</CODE> instance.
     * @param className The fully-qualified class name of the desired instance
     * @param parameters The desired parameters for the instance
     * @return Instance of <CODE>TimeFunction</CODE> based on the
     * (fully-qualified) class name and the parameters. 
     * @throws IllegalArgumentException If the className is <CODE>null</CODE> or
     * a class with that name cannot be found.
     */
    public static TimeFunction getInstance(String className, Object... parameters) {
        if (className == null) {
            throw new IllegalArgumentException("null class name");
        }
        // First check cache
        Class<?> timeFunctionClass = cache.get(className);
        if (timeFunctionClass == null) {
            timeFunctionClass = findFullyQualifiedNameFor(className);
            if (timeFunctionClass == null) {
                // The name may be the distribution - try appending "Function"
                timeFunctionClass = findFullyQualifiedNameFor(className + "Function");
            }
            // All attempts have failed, trying with RandomVariate
            if (timeFunctionClass == null)
        		return new RandomFunction(RandomVariateFactory.getInstance(className, parameters));
            else
                cache.put(className, timeFunctionClass);
        }

        TimeFunction instance = null;
        try {
            instance = (TimeFunction) timeFunctionClass.newInstance();
        } 
        catch (InstantiationException e) { throw new RuntimeException(e); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
        instance.setParameters(parameters);
        return instance;
    }
    
    /**
     * Adds the given fully qualified package name to the list of packages
     * that will be searched when attempting to find RandomVariates by name.
     **/
    public static void addSearchPackage(String newPackage) {
        searchPackages.add(newPackage);
    }
    
    /**
     * Sets the list of packages that will be searched when attempting to find
     * a RandomVariate by name.  Replaces existing search packages.
     * @param packages New Set of search packages
     **/
    public static void setSearchPackages(Set<String> packages) {
        searchPackages = new LinkedHashSet<String>(packages);
    }
    
    /**
     * Returns a copy of the packages that will be searched when attempting to 
     * find a RandomVariate by name.
     * @return Copy of search packages Set.
     **/
    public static Set<String> getSearchPackages() { 
        return new LinkedHashSet<String>(searchPackages); 
    }
    
    /**
     * Finds the TimeFunction Class corresponding to the given name. First
     * attempts to find the TimeFunction assuming the the name is fully qualified.
     * Then searches the "search packages." The search path defaults to "es.ull.iis.function"
     * but additional search packages can be added.
     * @see #addSearchPackage(String)
     * @see #setSearchPackages(Set)
     **/
    public static Class<?> findFullyQualifiedNameFor(String className) {
        Class<?> theClass = null;
        //        First see if name passed is "fully qualified"
        try {
            theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
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
                if (!es.ull.iis.function.TimeFunction.class.isAssignableFrom(theClass)) {
                    continue;
                }
            } catch (ClassNotFoundException e) { continue; }
        }
        if (verbose) {System.out.println("returning " + theClass);}
        return theClass;
    }
}
