/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Node extends Location {
	/**
	 * 
	 */
	public Node(String description, TimeFunction delayAtExit, int size) {
		super(description, delayAtExit, size);
	}
	
	/**
	 * A new node with no capacity constrains.
	 */
	public Node(String description, TimeFunction delayAtExit) {
		super(description, delayAtExit);
	}
	
	@Override
	public Location getLocation() {
		return this;
	}

}
