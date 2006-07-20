/**
 * 
 */
package es.ull.isaatc.simulation.state;

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
	protected int currentSFId = -1;
	
	/**
	 * 
	 * @param elemId
	 * @param elemTypeId
	 * @param fState
	 * @param pending
	 * @param requested
	 * @param currentSFId
	 */
	public ElementState(int elemId, int elemTypeId, FlowState fState, int []pending, int[][]requested, int currentSFId) {
		this.elemId = elemId;
		this.elemTypeId = elemTypeId;
		this.flowState = fState;
		this.currentSFId = currentSFId;
		this.pending = pending;
		this.requested = requested;
	}
	
	
	/**
	 * 
	 * @param elemId
	 * @param elemTypeId
	 * @param fState
	 * @param pending
	 * @param requested
	 */
	public ElementState(int elemId, int elemTypeId, FlowState fState, int []pending, int[][]requested) {
		this.elemId = elemId;
		this.elemTypeId = elemTypeId;
		this.flowState = fState;
		this.pending = pending;
		this.requested = requested;
	}
	
	/**
	 * @return Returns the currentSFId.
	 */
	public int getCurrentSFId() {
		return currentSFId;
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
		if (currentSFId != -1)
			str.append("Current SF" + currentSFId);
		str.append("\r\n" + pending);
		str.append("\r\n" + requested);
		return str.toString();
	}
}
