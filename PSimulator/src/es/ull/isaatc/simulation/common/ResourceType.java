/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ResourceType extends VariableStoreSimulationObject, Describable {
	
	/* User methods */
	public long beforeRoleOn();
	public void afterRoleOn();
	public long beforeRoleOff();
	public void afterRoleOff();

}
