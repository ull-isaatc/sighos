/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.SimulationObjectInfo;

/**
 * "Listens" and processes the information of all the simulation objects.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObjectListener extends EventListener {
	/**
	 * Processes the information contained in an event.
	 * @param info Event which contains information about a component of the simulation.
	 */
	void infoEmited(SimulationObjectInfo info);
}
