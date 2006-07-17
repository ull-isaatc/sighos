/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.EventListener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface InfoListener extends EventListener {
	void infoEmited(SimulationInfo info);
}
