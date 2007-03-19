/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive atribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends DescSimulationObject {
	/** Element's priority in an activity queue. Minimum value: 0. */
	protected int priority = 0;
	
	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(int id, Simulation simul, String description) {
		super(id, simul, description);
	}

	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(int id, Simulation simul, String description, int priority) {
		super(id, simul, description);
		this.priority = priority;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	public double getTs() {
		return Double.NaN;
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
