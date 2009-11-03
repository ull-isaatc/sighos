/**
 * 
 */
package es.ull.isaatc.simulation.model;

import java.util.HashMap;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends VariableStoreModelObject implements es.ull.isaatc.simulation.common.ElementType {
	/** Element's priority in an activity queue. Minimum value: 0. */
	protected int priority = 0;
    /** A brief description of the element type */
    protected final String description;
    protected HashMap<String, Object> elementVariables;
	
	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param model Model this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(int id, Model model, String description) {
		this(id, model, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param model Model this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(int id, Model model, String description, int priority) {
		super(id, model);
		this.priority = priority;
		this.description = description;
		model.add(this);
		elementVariables = new HashMap<String, Object>();
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	public void addElementVar(String name, Object value) {
		elementVariables.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Describable#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.util.Prioritizable#getPriority()
	 */
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

	public HashMap<String,  Object> getElementValues() {
		return elementVariables;
	}
}
