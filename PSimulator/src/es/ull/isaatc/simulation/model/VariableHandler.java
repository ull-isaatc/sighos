/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.Collection;

/**
 * A class implementing this interface can manipulate model variables by means of one or several
 * user methods. These methods must have been previously defined into the class, normally in a hashmap or
 * similar structure, which would make easier to access them.  
 * @author Iván Castilla Rodríguez
 *
 */
public interface VariableHandler {
	/**
	 * Defines the body of a user method predefined in this class. If the method does not exist, false 
	 * is returned; else it returns true.
	 * @param method The name of a predefined method in this class
	 * @param body The content of the method, expressed in SIGHOS language
	 */
	boolean setMethod(String method, String body);
	
	/**
	 * Returns the body of the specified method
	 * @param method The name of the method
	 * @return The body of the specified method
	 */
	String getBody(String method);
	
	/**
	 * Returns the signature and return parameter of the specified method
	 * @param method The name of the method
	 * @return The signature and return parameter of the specified method
	 */
	String getCompleteMethod(String method);

	/**
	 * Returns the "import" statement associated to this class
	 * @return The "import" statement associated to this class
	 */
	String getImports();

	/**
	 * Sets the value of the "import" statement
	 * @param imports The new value for the "import" statement.
	 */
	void setImports(String imports);
	
	/**
	 * Returns a collection containing all the method's names. 
	 * @return A collection containing all the method's names.
	 */
	Collection<String> getMethods();
}
