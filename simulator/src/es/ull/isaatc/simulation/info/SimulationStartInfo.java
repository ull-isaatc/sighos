/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * Information related to the beggining of a simulation. It includes the cpu time when
 * the simulation started and the first identifier assigned to an element. 
 * @author Iván Castilla Rodríguez
 */
public class SimulationStartInfo extends SimulationInfo {
	private static final long serialVersionUID = -1180903996644859066L;
	/** CPU time when the simulation started */
	private long iniT;
	/** The first identifier assigned to an element */
	private int firstElementId;

	/**
	 * Creates an information related to the beggining of the simulation.
	 * @param simul Simulation which produced this information
	 * @param iniT CPU time when the simulation started
	 * @param firstElementId The first identifier assigned to an element
	 */
	public SimulationStartInfo(Simulation simul, long iniT, int firstElementId) {
		super(simul);
		this.iniT = iniT;
		this.firstElementId = firstElementId;
	}

	/**
	 * Returns the CPU time when the simulation started.
	 * @return The CPU time when the simulation started.
	 */
	public long getIniT() {
		return iniT;
	}

	/**
	 * Returns the fist identifier assigned to an element.
	 * @return The fist identifier assigned to an element.
	 */
	public int getFirstElementId() {
		return firstElementId;
	}

	/**
	 * Returns the simulation which produced this information.
	 * @return Simulation which produced this information
	 */
	public Simulation getSimulation() {
		return (Simulation)source;
	}
}
