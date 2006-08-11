/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of a simulation. The state of a simulation consists on the state
 * of its logical processes, elements and resources.
 * @author Iván Castilla Rodríguez
 */
public class SimulationState implements State {
	/** The state of the logical processes belonging to this simulation */
	protected ArrayList<LogicalProcessState> lpStates;
	/** The state of the elements belonging to this simulation */
	protected ArrayList<ElementState> elemStates;
	/** The state of the resources belonging to this simulation */
	protected ArrayList<ResourceState> resStates;
	/** Last element created in this simulation */
	protected int lastElemId;
	/** Last single flow created in this simulation */
	protected int lastSFId;
	/** Final timestamp of this simulation */
	protected double endTs;
	
	/**
	 * @param lastElemId Last element created in this simulation
	 * @param lastSFId Last single flow created in this simulation
	 * @param endTs Final timestamp of this simulation
	 */
	public SimulationState(int lastElemId, int lastSFId, double endTs) {
		lpStates = new ArrayList<LogicalProcessState>();
		elemStates = new ArrayList<ElementState>();
		resStates = new ArrayList<ResourceState>();
		this.lastElemId = lastElemId;
		this.lastSFId = lastSFId;
		this.endTs = endTs;
	}
	
	/**
	 * Includes the state of a logical process.
	 * @param lpState The state of a LP belonging to this simulation
	 */
	public void add(LogicalProcessState lpState) {
		lpStates.add(lpState);
	}
	
	/**
	 * Includes the state of an element
	 * @param eState The state of an element belonging to this simulation
	 */
	public void add(ElementState eState) {
		elemStates.add(eState);
	}

	/**
	 * Includes the state of a resource
	 * @param rState The state of a resource belonging to this simulation
	 */
	public void add(ResourceState rState) {
		resStates.add(rState);
	}
	
	/**
	 * @return The state of the elements belonging to this simulation.
	 */
	public ArrayList<ElementState> getElemStates() {
		return elemStates;
	}

	/**
	 * @return The state of the resources belonging to this simulation.
	 */
	public ArrayList<ResourceState> getResStates() {
		return resStates;
	}

	/**
	 * @return Final timestamp of this simulation.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * @return Last element created in this simulation.
	 */
	public int getLastElemId() {
		return lastElemId;
	}

	/**
	 * @return Last single flow created in this simulation.
	 */
	public int getLastSFId() {
		return lastSFId;
	}

	/**
	 * @return The state of the LPs belonging to this simulation.
	 */
	public ArrayList<LogicalProcessState> getLpStates() {
		return lpStates;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("SIMULATION(" + endTs + ")\tLast E: " + lastElemId + "\tLast SF: " + lastSFId);
		for (ElementState es : elemStates)
			str.append(es + "\r\n");
		for (ResourceState rs : resStates)
			str.append(rs + "\r\n");
		for (LogicalProcessState lps : lpStates)
			str.append(lps + "\r\n");
		return str.toString();
	}
	
}
