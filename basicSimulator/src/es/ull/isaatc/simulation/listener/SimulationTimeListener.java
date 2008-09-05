/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;

/**
 * Listens the duration of a simulation.
 * @author Iván Castilla Rodríguez
 */
public class SimulationTimeListener implements SimulationListener {
	/** Initial CPU time (miliseconds). */
	protected long iniT;
	/** Final CPU time (miliseconds). */
	protected long endT;

	
	public SimulationTimeListener() {		
	}
	
	/**
	 * Returns the initial CPU time (miliseconds).
	 * @return Initial CPU time (miliseconds).
	 */
	public long getIniT() {
		return iniT;
	}

	/**
	 * Returns the final CPU time (miliseconds).
	 * @return Final CPU time (miliseconds).
	 */
	public long getEndT() {
		return endT;
	}

	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
	}

	public void infoEmited(SimulationEndInfo info) {
		endT = info.getEndT();
	}

	@Override
	public String toString() {
		return new String("Simulation time:\t" + (endT - iniT) + "\n");
	}	
}
