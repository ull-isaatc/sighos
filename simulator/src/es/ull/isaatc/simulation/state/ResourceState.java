/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceState implements State {
	protected int resId;
	protected int currentElemId = -1;
	protected int currentRTId = -1;
	protected boolean timeOut = false;
	protected int validTTEs;
	protected ArrayList<Integer> currentRoles;
	
	/**
	 * @param resId
	 * @param currentElemId
	 * @param currentRTId
	 * @param timeOut
	 */
	public ResourceState(int resId, int validTTEs, int currentElemId, int currentRTId, boolean timeOut) {
		this.resId = resId;
		this.currentElemId = currentElemId;
		this.currentRTId = currentRTId;
		this.timeOut = timeOut;
		this.validTTEs = validTTEs;
		currentRoles = new ArrayList<Integer>();
	}
	
	/**
	 * @param resId
	 */
	public ResourceState(int resId, int validTTEs) {
		this.resId = resId;
		this.validTTEs = validTTEs;
	}
	
	public void add(int rtId) {
		currentRoles.add(rtId);
	}
	/**
	 * @return Returns the currentElemId.
	 */
	public int getCurrentElemId() {
		return currentElemId;
	}
	/**
	 * @return Returns the currentRTId.
	 */
	public int getCurrentRTId() {
		return currentRTId;
	}
	/**
	 * @return Returns the resId.
	 */
	public int getResId() {
		return resId;
	}
	/**
	 * @return Returns the validTTEs.
	 */
	public int getValidTTEs() {
		return validTTEs;
	}

	/**
	 * @return Returns the currentRoles.
	 */
	public ArrayList<Integer> getCurrentRoles() {
		return currentRoles;
	}
	/**
	 * @return Returns the timeOut.
	 */
	public boolean isTimeOut() {
		return timeOut;
	}
	
}
