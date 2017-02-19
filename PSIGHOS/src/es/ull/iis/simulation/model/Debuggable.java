/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * Indicates that an object can print debug messages
 * @author Iván Castilla Rodríguez
 *
 */
public interface Debuggable {
	/**
	 * Prints a debug message.
	 * @param message Message to be printed
	 */
    void debug(String message);

	/**
	 * Prints an error message.
	 * @param message Message to be printed
	 */
    void error(String message);
    
    /**
     * Checks if debug mode is enabled.
     * @return True if debug is enabled; false in other case
     */
	boolean isDebugEnabled();
    
}
