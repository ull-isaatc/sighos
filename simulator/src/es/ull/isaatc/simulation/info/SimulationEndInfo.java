/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * Information related to the end of a simulation. It includes the cpu time when
 * the simulation finished and the last identifier assigned to an element. 
 * @author Iván Castilla Rodríguez
 */
public class SimulationEndInfo extends SimulationInfo {
	private static final long serialVersionUID = -88745617314199633L;
	/** CPU time when the simulation finished */
	private long endT;
	/** The last identifier assigned to an element */
	private int lastElementId;

	/**
	 * Creates an information related to the end of the simulation.
	 * @param simul Simulation which produced this information
	 * @param endT CPU time when the simulation finished
	 * @param lastElementId The last identifier assigned to an element
	 */
	public SimulationEndInfo(Simulation simul, long endT, int lastElementId) {
		super(simul);
		this.endT = endT;
		this.lastElementId = lastElementId;
	}

	/**
	 * Returns the CPU time when the simulation finished.
	 * @return The CPU time when the simulation finished.
	 */
	public long getEndT() {
		return endT;
	}

	/**
	 * Returns the last identifier assigned to an element.
	 * @return The last identifier assigned to an element.
	 */
	public int getLastElementId() {
		return lastElementId;
	}

	/**
	 * Returns the simulation which produced this information.
	 * @return Simulation which produced this information
	 */
	public Simulation getSimulation() {
		return (Simulation)source;
	}
}
