/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;

/**
 * "Listens" and processes the information of a simulation.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationListener extends EventListener {
	
	/**
	 * Processes the information about the beggining of the simulation. This method can be used
	 * to initialize structures.
	 * @param info Event which contains information about the beggining  of the simulation.
	 */
	void infoEmited(SimulationStartInfo info);
	
	/**
	 * Processes the information about the ending of the simulation. This method can be used 
	 * to finalize structures or display the information recovered. 
	 * @param info Event which contains information about the ending of the simulation.
	 */
	void infoEmited(SimulationEndInfo info);
}
