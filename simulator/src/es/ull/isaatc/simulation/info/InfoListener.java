/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.EventListener;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface InfoListener extends EventListener {
	void infoEmited(SimulationInfo info);
}
