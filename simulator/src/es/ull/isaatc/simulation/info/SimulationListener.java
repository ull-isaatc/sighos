/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.EventListener;

/**
 * "Listens" and processes the information of a simulation.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationListener extends EventListener {
	/**
	 * Processes the information contained in an event.
	 * @param info Event which contains information about a component of the simulation.
	 */
	void infoEmited(SimulationObjectInfo info);
	
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

	/**
	 * Processes the information about a simulation time advancing. This method can be used 
	 * to control the simulation time, even by stopping the simulation. 
	 * @param info Event which contains information about the simulation time advancing.
	 */
	void infoEmited(TimeChangeInfo info);
}
