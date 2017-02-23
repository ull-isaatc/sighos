/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * The type of a resource. Defines roles or specializations of the resources.<p>
 * A user can define customized code associated to different events where this class
 * is involved:
 * <ul>
 * <li>When a resource is activated for this type: {@link #beforeRoleOn()}, {@link #afterRoleOn()}</li>
 * <li>When a resource is deactivated for this type: {@link #beforeRoleOff()}, {@link #afterRoleOff()}</li>
 * </ul>
 * @author Iván Castilla Rodríguez
 */
public interface ResourceTypeEngine {
	
}
