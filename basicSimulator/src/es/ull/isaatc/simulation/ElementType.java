/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.Prioritizable;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive atribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends SimulationObject implements Describable, Prioritizable {
	/** Element's priority in an activity queue. Minimum value: 0. */
	protected int priority = 0;
    /** A brief description of the element type */
    protected String description;
	
	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(int id, Simulation simul, String description) {
		this(id, simul, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(int id, Simulation simul, String description, int priority) {
		super(id, simul);
		this.priority = priority;
		this.description = description;
		simul.add(this);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

    /**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

    /**
     * Returns the element type's priority.
	 * @return Returns the priority.
	 */
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
