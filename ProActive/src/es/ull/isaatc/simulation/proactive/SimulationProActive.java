/**
 * 
 */
package es.ull.isaatc.simulation.proactive;

import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.listener.SimulationListener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class SimulationProActive extends StandAloneLPSimulation {
	
	public SimulationProActive() {
	}

	public SimulationProActive(String description) {
		super(description);
	}

	public String [] getListenerResults() {
		String []listenerRes = new String[getListeners().size()];
		int count = 0;
		for (SimulationListener listener : getListeners())
			listenerRes[count++] = listener.toString();
		return listenerRes;
	}
}
