/**
 * 
 */
package es.ull.isaatc.simulation.info;

import es.ull.isaatc.simulation.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationEndInfo extends SimulationInfo {
	private long endT;
	private int lastElementId;
	/**	 */
	private static final long serialVersionUID = -88745617314199633L;

	public SimulationEndInfo(Simulation simul, long endT, int lastElementId) {
		super(simul);
		this.endT = endT;
		this.lastElementId = lastElementId;
	}

	/**
	 * @return Returns the endT.
	 */
	public long getEndT() {
		return endT;
	}

	/**
	 * @return Returns the lastElementId.
	 */
	public int getLastElementId() {
		return lastElementId;
	}

	public Simulation getSimulation() {
		return (Simulation)source;
	}
}
