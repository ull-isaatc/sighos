/**
 * 
 */
package es.ull.iis.simulation.sequential;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive attribute
 * and is used for statistic issues.
 * @author Iván Castilla Rodríguez
 */
public class ElementType extends VariableStoreSimulationObject {
    private final es.ull.iis.simulation.model.ElementType modelET;
	
	/**
	 * Creates a new element type.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 * @param priority The priority to set.
	 */
	public ElementType(Simulation simul, es.ull.iis.simulation.model.ElementType modelET) {
		super(simul.getNextElementTypeId(), simul);
		this.modelET = modelET;
		simul.add(this);
	}

	/**
	 * @return the modelET
	 */
	public es.ull.iis.simulation.model.ElementType getModelET() {
		return modelET;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return modelET.getObjectTypeIdentifier();
	}

	public String getDescription() {
		return modelET.getDescription();
	}

	public int getPriority() {
		return modelET.getPriority();
	}

	/**
	 * Sets a value for the priority of the element type
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		modelET.setPriority(priority);
	}
}
