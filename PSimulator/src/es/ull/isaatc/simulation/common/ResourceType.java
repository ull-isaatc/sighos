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
	public long beforeRoleOn();
	public void afterRoleOn();
	public long beforeRoleOff();
	public void afterRoleOff();

}
