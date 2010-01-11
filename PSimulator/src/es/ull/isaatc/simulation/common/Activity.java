/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Prioritizable;

/**
 * A task which requires certain amount and type of resources to be performed.
 * @author Iván Castilla Rodríguez
 *
 */
public interface Activity extends VariableStoreSimulationObject, Describable, Prioritizable {
	/**
	 * Returns the specified workgroup.
	 * @param wgId Workgroup's identifier
	 * @return the specified workgroup
	 */
	public ActivityWorkGroup getWorkGroup(int wgId);
}
