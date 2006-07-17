/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ElementState implements State {
	protected int elemId;
	protected int elemTypeId;
	protected FlowState flowState;
    /** Amount of pending presential activities (pending[0]) and non-presential 
    ones (pending[1]) */
	protected int []pending;
    /** Requested presential single flows (requested[0]) and non-presential 
    ones (requested[1]) */
	protected int[][]requested;
	// If the element is currently performing an activity
	protected int currentActId = -1;
	protected int currentWGId = -1;
	protected ArrayList<Integer> caughtResources = null;
	
	/**
	 * @param elemId
	 * @param elemTypeId
	 * @param currentActId
	 * @param currentWGId
	 */
	public ElementState(int elemId, int elemTypeId, FlowState fState, int currentActId, int currentWGId, int []pending, int[][]requested) {
		this.elemId = elemId;
		this.elemTypeId = elemTypeId;
		this.flowState = fState;
		this.currentActId = currentActId;
		this.currentWGId = currentWGId;
		this.pending = pending;
		this.requested = requested;
		caughtResources = new ArrayList<Integer>();
	}
	
	
	/**
	 * @param elemId
	 * @param elemTypeId
	 */
	public ElementState(int elemId, int elemTypeId, FlowState fState, int []pending, int[][]requested) {
		this.elemId = elemId;
		this.elemTypeId = elemTypeId;
		this.flowState = fState;
		this.pending = pending;
		this.requested = requested;
	}
	
	public void add(int resId) {
		caughtResources.add(resId);
	}


	/**
	 * @return Returns the caughtResources.
	 */
	public ArrayList<Integer> getCaughtResources() {
		return caughtResources;
	}


	/**
	 * @return Returns the currentActId.
	 */
	public int getCurrentActId() {
		return currentActId;
	}


	/**
	 * @return Returns the currentWGId.
	 */
	public int getCurrentWGId() {
		return currentWGId;
	}


	/**
	 * @return Returns the elemId.
	 */
	public int getElemId() {
		return elemId;
	}


	/**
	 * @return Returns the elemTypeId.
	 */
	public int getElemTypeId() {
		return elemTypeId;
	}


	/**
	 * @return Returns the flowState.
	 */
	public FlowState getFlowState() {
		return flowState;
	}


	/**
	 * @return Returns the pending.
	 */
	public int[] getPending() {
		return pending;
	}


	/**
	 * @return Returns the requested.
	 */
	public int[][] getRequested() {
		return requested;
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer("E" + elemId + "(" + elemTypeId + ")\r\n");
		str.append("\tFLOW: " + flowState.toString() + "\r\n");
		if (currentWGId != -1) {
			str.append("Current A" + currentActId + " (WG" + currentWGId + "):");
			for (Integer rtId : caughtResources)
				str.append(" " + rtId);
		}
		str.append("\r\n" + pending);
		str.append("\r\n" + requested);
		return str.toString();
	}
}
