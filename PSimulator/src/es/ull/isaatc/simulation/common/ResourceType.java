/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Describable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface ResourceType extends SimulationObject, Describable {
	
	/* User methods */
	public double beforeRoleOn();
	public void afterRoleOn();
	public double beforeRoleOff();
	public void afterRoleOff();

}
