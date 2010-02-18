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
     * Searches and returns the WG with the specified identifier.
     * @param wgId The identifier of the searched WG 
     * @return A WG defined in this activity with the specified identifier
     */
	public ActivityWorkGroup getWorkGroup(int wgId);
}
