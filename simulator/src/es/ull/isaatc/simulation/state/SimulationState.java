/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationState implements State {
	protected ArrayList<LogicalProcessState> lpStates;
	protected ArrayList<ElementState> elemStates;
	protected ArrayList<ResourceState> resStates;
	protected int lastElemId;
	protected int lastSFId;
	protected double endTs;
	
	/**
	 * @param lastElemId
	 * @param lastSFId
	 * @param startTs
	 * @param endTs
	 */
	public SimulationState(int lastElemId, int lastSFId, double endTs) {
		lpStates = new ArrayList<LogicalProcessState>();
		elemStates = new ArrayList<ElementState>();
		resStates = new ArrayList<ResourceState>();
		this.lastElemId = lastElemId;
		this.lastSFId = lastSFId;
		this.endTs = endTs;
	}
	
	public void add(LogicalProcessState lpState) {
		lpStates.add(lpState);
	}
	
	public void add(ElementState eState) {
		elemStates.add(eState);
	}

	public void add(ResourceState rState) {
		resStates.add(rState);
	}
	
	/**
	 * @return Returns the elemStates.
	 */
	public ArrayList<ElementState> getElemStates() {
		return elemStates;
	}

	/**
	 * @return Returns the resStates.
	 */
	public ArrayList<ResourceState> getResStates() {
		return resStates;
	}

	/**
	 * @return Returns the endTs.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * @return Returns the lastElemId.
	 */
	public int getLastElemId() {
		return lastElemId;
	}

	/**
	 * @return Returns the lastSFId.
	 */
	public int getLastSFId() {
		return lastSFId;
	}

	/**
	 * @return Returns the lpStates.
	 */
	public ArrayList<LogicalProcessState> getLpStates() {
		return lpStates;
	}

}
