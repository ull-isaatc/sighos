/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * Stores the state of a resource. The state of a resource consists on the amount of
 * valid timetable entries, the current single flow (if it exists), and the current roles.
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceState implements State {
	/** This resource's identifier */
	protected int resId;
    /** A counter of the valid timetable entries which this resource is following */
	protected int validTTEs;
	/** The resource types this resource is currently available for, and the end of the availability. */
	protected TreeMap<Integer, Double> currentRoles;
	// If the resource is currently being used by an element
	/** Single flow that is currently using this resource */
	protected int currentSFId = -1;
	/** Element that is currently using this resource */
	protected int currentElemId = -1;
	/** Resource type that this resource is currently being used for */
	protected int currentRTId = -1;
    /** If true, indicates that this resource is being used after its availability time has expired */
	protected boolean timeOut = false;
	
	/**
	 * @param resId This resource's identifier
	 * @param validTTEs A counter of the valid timetable entries which this resource is following
	 * @param currentSFId Single flow that is currently using this resource
	 * @param currentElemId Element that is currently using this resource
	 * @param currentRTId Resource type that this resource is currently being used for
	 * @param timeOut If true, indicates that this resource is being used after its availability time has expired
	 */
	public ResourceState(int resId, int validTTEs, int currentSFId, int currentElemId, int currentRTId, boolean timeOut) {
		this.resId = resId;
		this.currentSFId = currentSFId;
		this.currentElemId = currentElemId;
		this.currentRTId = currentRTId;
		this.timeOut = timeOut;
		this.validTTEs = validTTEs;
		currentRoles = new TreeMap<Integer, Double>();
	}
	
	/**
	 * @param resId This resource's identifier
	 * @param validTTEs A counter of the valid timetable entries which this resource is following
	 */
	public ResourceState(int resId, int validTTEs) {
		this.resId = resId;
		this.validTTEs = validTTEs;
		currentRoles = new TreeMap<Integer, Double>();
	}

	/**
	 * Adds a resource type to the current role list.
	 * @param rtId A resource type this resource is currently available for
	 * @param ts Timestamp when the availability of this resource finishes for this resource type. 
	 */
	public void add(int rtId, double ts) {
		currentRoles.put(rtId, ts);
	}
	/**
	 * @return The identifier of the single flow that is currently using this resource.
	 */
	public int getCurrentSFId() {
		return currentSFId;
	}

	/**
	 * @return The identifier of the element that is currently using this resource.
	 */
	public int getCurrentElemId() {
		return currentElemId;
	}
	/**
	 * @return The identifier of the resource type that this resource is currently being used for.
	 */
	public int getCurrentRTId() {
		return currentRTId;
	}
	/**
	 * @return This resource's identifier.
	 */
	public int getResId() {
		return resId;
	}
	/**
	 * @return The counter of the valid timetable entries which this resource is following.
	 */
	public int getValidTTEs() {
		return validTTEs;
	}

	/**
	 * @return The list of resource types this resource is currently available for.
	 */
	public TreeMap<Integer, Double> getCurrentRoles() {
		return currentRoles;
	}
	/**
	 * @return True if this resource is being used after its availability time has expired.
	 */
	public boolean isTimeOut() {
		return timeOut;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("R" + resId + " ( ");
		for (Entry<Integer, Double> entry : currentRoles.entrySet())
			str.append(entry.getKey() + "(" + entry.getValue() + ") ");
		if (timeOut)
			str.append(")\tTIMEOUT\r\n");
		else
			str.append(")\r\n");
		str.append("Valid time table entries: " + validTTEs);
		if (currentSFId != -1)
			str.append("Current: SF " + currentSFId + "\tELEM" + currentElemId + "\tRES_TYPE" + currentRTId);
		return str.toString();
	}

}
