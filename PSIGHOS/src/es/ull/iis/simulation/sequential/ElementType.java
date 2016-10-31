/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.HashMap;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends VariableStoreSimulationObject implements es.ull.iis.simulation.core.ElementType {
	/** Element's priority in an activity queue. Minimum value: 0. */
	protected int priority = 0;
    /** A brief description of the element type */
    protected final String description;
    protected HashMap<String, Object> elementVariables;
	
	/**
	 * Creates a new element type.
	 * @param simul Simul this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(Simulation simul, String description) {
		this(simul, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param simul Simul this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(Simulation simul, String description, int priority) {
		super(simul.getNextElementTypeId(), simul);
		this.priority = priority;
		this.description = description;
		simul.add(this);
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
	 * @see es.ull.iis.simulation.Describable#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.util.Prioritizable#getPriority()
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
