/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface WorkGroup {
	ResourceType[] getResourceTypes();
	int[] getNeeded();
}