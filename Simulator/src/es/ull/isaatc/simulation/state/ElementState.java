/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * Stores the state of an element. The state of an element consists on the state of its flow,
 * the requested and pending flows, and the current single flow (if being used).
 * @author Iván Castilla Rodríguez
 */
public class ElementState implements State {
	/** This element's identifier */ 
	protected int elemId;
	/** The element type this element belongs to */
	protected int elemTypeId;
	/** The state of the flow of the element */ 
	protected FlowState flowState;
    /** Amount of pending presential activities (pending[0]) and non-presential 
    ones (pending[1]) */
	protected int []pending;
    /** Requested presential single flows (requested[0]) and non-presential 
    ones (requested[1]) */
	protected int[][]requested;
	/** The current single flow, if the element is currently performing an activity */	
	protected int currentSFId = -1;
	
	/**
	 * @param elemId This element's identifier
	 * @param elemTypeId The element type this element belongs to
	 * @param fState The state of the flow of the element
	 * @param pending Amount of pending presential activities (pending[0]) and non-presential ones (pending[1])
	 * @param requested Requested presential single flows (requested[0]) and non-presential ones (requested[1])
	 * @param currentSFId The current single flow, if the element is currently performing an activity
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
	 * @param elemId This element's identifier
	 * @param elemTypeId The element type this element belongs to
	 * @param fState The state of the flow of the element
	 * @param pending Amount of pending presential activities (pending[0]) and non-presential ones (pending[1])
	 * @param requested Requested presential single flows (requested[0]) and non-presential ones (requested[1])
	 */
	public ElementState(int elemId, int elemTypeId, FlowState fState, int []pending, int[][]requested) {
		this.elemId = elemId;
		this.elemTypeId = elemTypeId;
		this.flowState = fState;
		this.pending = pending;
		this.requested = requested;
	}
	
	/**
	 * @return The single flow's identifier, if the element is currently performing an activity.
	 * -1 in other case.
	 */
	public int getCurrentSFId() {
		return currentSFId;
	}

	/**
	 * @return This element's identifier.
	 */
	public int getElemId() {
		return elemId;
	}


	/**
	 * @return The element type this element belongs to.
	 */
	public int getElemTypeId() {
		return elemTypeId;
	}


	/**
	 * @return The state of the flow of this element.
	 */
	public FlowState getFlowState() {
		return flowState;
	}


	/**
	 * @return The total amount of pending activities.
	 */
	public int[] getPending() {
		return pending;
	}


	/**
	 * @return The requested single flows.
	 */
	public int[][] getRequested() {
		return requested;
	}
	
	@Override
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
