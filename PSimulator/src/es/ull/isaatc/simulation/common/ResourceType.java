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
	public double beforeRoleOn();
	public void afterRoleOn();
	public double beforeRoleOff();
	public void afterRoleOff();

}
