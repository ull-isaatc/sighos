/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ResourceType extends VariableStoreSimulationObject, Describable {
	
	/* User methods */
	public double beforeRoleOn();
	public void afterRoleOn();
	public double beforeRoleOff();
	public void afterRoleOff();

}
