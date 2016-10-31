/**
 * 
 */
package es.ull.iis.simulation.parallel;

import java.util.HashMap;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends VariableStoreSimulationObject implements es.ull.iis.simulation.core.ElementType {
	/** Element's priority in an activity queue. Minimum value: 0. */
	protected int priority;
    /** A brief description of the element type */
    protected final String description;
    protected final HashMap<String, Object> elementValues = new HashMap<String, Object>();
	
	/**
	 * Creates a new element type.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(Simulation simul, String description) {
		this(simul, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(Simulation simul, String description, int priority) {
		super(simul.getNextElementTypeId(), simul);
		this.priority = priority;
		this.description = description;
		simul.add(this);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	public void addElementVar(String name, Object value) {
		elementValues.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Describable#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.util.Prioritizable#getPriority()
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

	public HashMap<String,  Object> getElementValues() {
		return elementValues;
	}
}
