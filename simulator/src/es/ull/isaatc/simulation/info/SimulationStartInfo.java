/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationStartInfo extends SimulationInfo {
	private long iniT;
	private int firstElementId;
	/**	 */
	private static final long serialVersionUID = -1180903996644859066L;

	public SimulationStartInfo(Simulation simul, long iniT, int firstElementId) {
		super(simul);
		this.iniT = iniT;
		this.firstElementId = firstElementId;
	}

	/**
	 * @return Returns the iniT.
	 */
	public long getIniT() {
		return iniT;
	}

	/**
	 * @return Returns the firstElementId.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	public Simulation getSimulation() {
		return (Simulation)source;
	}
}
