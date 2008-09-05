/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * "Listens" and processes the information about simulation time.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeChangeListener extends EventListener {
	/**
	 * Processes the information about a simulation time advancing. This method can be used 
	 * to control the simulation time, even by stopping the simulation. 
	 * @param info Event which contains information about the simulation time advancing.
	 */
	void infoEmited(TimeChangeInfo info);
}
