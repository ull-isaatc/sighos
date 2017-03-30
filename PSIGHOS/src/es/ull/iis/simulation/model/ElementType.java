/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.TreeMap;

import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.util.Prioritizable;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends VariableStoreSimulationObject implements Describable, Prioritizable {
	/** Element's priority in an activity queue. Minimum value: 0. */
	private int priority = 0;
    /** A brief description of the element type */
    private final String description;
    protected final TreeMap<String, Object> elementValues = new TreeMap<String, Object>();
    
	/**
	 * Creates a new element type with the highest priority.
	 * @param description A short text describing this element type.
	 */
	public ElementType(Simulation model, String description) {
		this(model, description, 0);
	}

	/**
	 * Creates a new element type.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(Simulation model, String description, int priority) {
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
	
	/**
	 * Adds a variable which will be associated to every element of this type. Once instantiated,
	 * each element has its own variable.
	 * @param name Variable name
	 * @param value Initial value of the variable.
	 */
	public void addElementVar(String name, Object value) {
		elementValues.put(name, value);
	}
	
	public TreeMap<String,  Object> getElementValues() {
		return elementValues;
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
	}
}
