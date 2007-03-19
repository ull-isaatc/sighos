/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * An indicator of a class which describes the way a generator creates elements 
 * when it's time to create them.
 * @author Iván Castilla Rodríguez
 */
public interface BasicElementCreator {
	/**
	 * Describes the way a generator creates elements when it's time to create them.
	 * @param gen The generator which wants to create elements.
	 */
	public void create(Generator gen);
}
