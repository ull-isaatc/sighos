package es.ull.iis.simulation.variable;

/**
 * Class created to define simulation's variables. This variables those 
 * variables are associated to a SimulationObjects.
 *  
 * @author ycallero
 *
 */
public interface Variable {
	
	/**
	 * Obtain de current Variable's value.
	 * @return Variable's value.
	 */
	public Number getValue(Object... params);
	
	/**
	 * Convert Variable's value to a string.
	 * @return String which represents Variable's value.
	 */
	public String toString();
}
