/**
 * 
 */
package es.ull.isaatc.simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementType extends DescSimulationObject {
	
	/**
	 * @param id
	 * @param simul
	 * @param description
	 */
	public ElementType(int id, Simulation simul, String description) {
		super(id, simul, description);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ET";
	}

	@Override
	public double getTs() {
		return 0;
	}
}
