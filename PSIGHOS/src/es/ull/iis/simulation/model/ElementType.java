/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.util.Prioritizable;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iv�n Castilla Rodr�guez
 */
public class ElementType extends ModelObject implements Describable, Prioritizable {
	/** Element's priority in an activity queue. Minimum value: 0. */
	private int priority = 0;
    /** A brief description of the element type */
    private final String description;
    
	/**
	 * Creates a new element type with the highest priority.
	 * @param description A short text describing this element type.
	 */
	public ElementType(Model model, String description) {
		this(model, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(Model model, String description, int priority) {
		super(model, model.getElementTypeList().size(), "ET");
		this.priority = priority;
		this.description = description;
		model.add(this);
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
