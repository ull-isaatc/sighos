/**
 * 
 */
package es.ull.isaatc.simulation.model;

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
}
