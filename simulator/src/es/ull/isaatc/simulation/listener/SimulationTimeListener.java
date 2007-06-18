/**
 * 
 */
package es.ull.isaatc.simulation.listener;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;

/**
 * Listens the duration of a simulation.
 * @author Iv�n Castilla Rodr�guez
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

	@Override
	public void infoEmited(SimulationObjectInfo info) {
	}

	@Override
	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
	}

	@Override
	public void infoEmited(SimulationEndInfo info) {
		endT = info.getEndT();
	}

	@Override
	public void infoEmited(TimeChangeInfo info) {
	}

	@Override
	public String toString() {
		return new String("Simulation time:\t" + (endT - iniT) + "\n");
	}	
}
