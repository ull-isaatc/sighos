/**
 * 
 */
package es.ull.iis.simulation.model.location;

import es.ull.iis.function.TimeFunction;

/**
 * A location representing a specific place.
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public class Node extends Location {
	/**
	 * Creates a node with capacity constrains.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 * @param capacity Total capacity of the location
	 */
	public Node(String description, TimeFunction delayAtExit, int capacity) {
		super(description, delayAtExit, capacity);
	}
	
	/**
	 * Creates a node with capacity constrains.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 * @param capacity Total capacity of the location
	 */
	public Node(String description, long delayAtExit, int capacity) {
		super(description, delayAtExit, capacity);
	}

	/**
	 * Creates a node with no capacity constrains.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 */
	public Node(String description, TimeFunction delayAtExit) {
		super(description, delayAtExit);
	}
	
	/**
	 * Creates a node with no capacity constrains.
	 * @param description A brief description of the location
	 * @param delayAtExit The time that it takes to exit (or go through) the location
	 */
	public Node(String description, long delayAtExit) {
		super(description, delayAtExit);
	}
	
	/**
	 * Creates a node with capacity constrains and no time to exit.
	 * @param description A brief description of the location
	 * @param capacity Total capacity of the location
	 */
	public Node(String description, int capacity) {
		super(description, capacity);
	}
	
	/**
	 * Creates a node with no capacity constrains and no time to exit.
	 * @param description A brief description of the location
	 */
	public Node(String description) {
		super(description);
	}
	
	@Override
	public Location getLocation() {
		return this;
	}

}
