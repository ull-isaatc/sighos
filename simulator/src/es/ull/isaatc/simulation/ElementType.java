/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * Describes a set of elements which have something in common. This is simply a descriptive atribute
 * and is used for statistic issues.
 * @author Iv�n Castilla Rodr�guez
 */
public class ElementType extends DescSimulationObject {
	
	/**
	 * Creates a new element type.
	 * @param id Element type's identifier.
	 * @param simul Simulation this element type belongs to.
	 * @param description A short text describing this element type.
	 */
	public ElementType(int id, Simulation simul, String description) {
		super(id, simul, description);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	// FIXME: tal vez los elementType no deber�an ser DescSimulationObject
	public double getTs() {
		return 0.0;
	}
}
