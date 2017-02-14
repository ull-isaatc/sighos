/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.core.Describable;
import es.ull.iis.util.Prioritizable;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType implements ModelObject, Describable, Prioritizable {
	/** Element's priority in an activity queue. Minimum value: 0. */
	private int priority = 0;
    /** A brief description of the element type */
    private final String description;

    
	/**
	 * Creates a new element type with the highest priority.
	 * @param description A short text describing this element type.
	 */
	public ElementType(String description) {
		this.priority = 0;
		this.description = description;
	}

	/**
	 * Creates a new element type.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(String description, int priority) {
		this.priority = priority;
		this.description = description;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets a value for the priority of the element type
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
