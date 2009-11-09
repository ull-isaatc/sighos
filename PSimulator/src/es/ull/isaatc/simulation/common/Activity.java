/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Prioritizable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface Activity extends VariableStoreSimulationObject, Describable, Prioritizable {
	public ActivityWorkGroup getWorkGroup(int wgId);
}
