/**
 * 
 */
package es.ull.isaatc.simulation.optGroupedThreaded;

/**
 * An indicator of a class that it can create elements 
 * @author Iv�n Castilla Rodr�guez
 */
public interface BasicElementCreator {
	/**
	 * Describes the way a generator creates elements when it's time to create them.
	 * @param gen The generator which wants to create elements.
	 */
	public void create(Generator gen);
}
