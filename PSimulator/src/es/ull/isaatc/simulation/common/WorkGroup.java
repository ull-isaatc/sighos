/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface WorkGroup {
	ResourceType[] getResourceTypes();
	int[] getNeeded();
}
