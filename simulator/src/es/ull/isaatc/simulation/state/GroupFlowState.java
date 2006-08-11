/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * The state of a group flow. The state of a group of flows consists on the state of each subflow
 * (or descendant flow) and the amount of finished descendant flows.
 * @author Iván Castilla Rodríguez
 */
public abstract class GroupFlowState extends FlowState {
	/** A list containing the descendant flows */
	protected ArrayList<FlowState> descendants;
	/** The amount of finished descendant flows */
	protected int finished;
	
	/**
	 * @param finished The amount of finished descendant flows
	 */
	public GroupFlowState(int finished) {
		this.finished = finished;
		descendants = new ArrayList<FlowState>();
	}
	
	/**
	 * Adds the state of a descendant flow
	 * @param flow A descendant flow
	 */
	public void add(FlowState flow) {
		descendants.add(flow);
	}

	/**
	 * @return The descendant flows.
	 */
	public ArrayList<FlowState> getDescendants() {
		return descendants;
	}

	/**
	 * @return The amount of finished descendant flows.
	 */
	public int getFinished() {
		return finished;
	}

}
