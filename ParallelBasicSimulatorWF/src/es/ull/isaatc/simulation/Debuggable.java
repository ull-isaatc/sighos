/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * Indicates that an object can emit debug messages
 * @author Iván Castilla Rodríguez
 *
 */
public interface Debuggable {
	/**
	 * Prints a debug message
	 * @param message Message to be printed
	 */
    void debug(String message);

	/**
	 * Prints an error message
	 * @param message Message to be printed
	 */
    void error(String message);
    
    /**
     * Checks if debug is enabled
     * @return True if debug is enabled; false in other case
     */
	boolean isDebugEnabled();
    
}
