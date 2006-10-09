/**
 * 
 */
package es.ull.isaatc.simulation.info;

/**
 * Listens the duration of a simulation.
 * @author Iván Castilla Rodríguez
 *
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

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationComponentInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		iniT = info.getIniT();
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.InfoListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		endT = info.getEndT();
	}

	// Nothing to do
	public void infoEmited(TimeChangeInfo info) {
	}

	@Override
	public String toString() {
		return new String("" + (endT - iniT));
	}	
}
