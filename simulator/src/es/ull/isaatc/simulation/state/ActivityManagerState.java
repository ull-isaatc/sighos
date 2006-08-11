/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of an activity manager (AM). The state of an AM consists on
 * the state of its activities and resource types.
 * @author Iván Castilla Rodríguez
 */
public class ActivityManagerState implements State {
	/** This activity manager's identifier */
	protected int amId;
	/** State of the activities contained in this AM */
	protected ArrayList<ActivityState> aStates;
	/** State of the resource types contained in this AM */
	protected ArrayList<ResourceTypeState> rtStates;
	
	/**
	 * @param amId This AM's identifier
	 */
	public ActivityManagerState(int amId) {
		this.amId = amId;
		aStates = new ArrayList<ActivityState>();
		rtStates = new ArrayList<ResourceTypeState>();
	}
	
	/**
	 * Adds the state of an activity.
	 * @param aState State of an activity
	 */
	public void add(ActivityState aState) {
		aStates.add(aState);
	}

	/**
	 * Adds the state of an resource type.
	 * @param aState State of an resource type
	 */
	public void add(ResourceTypeState rtState) {
		rtStates.add(rtState);
	}

	/**
	 * @return This AM's identifier.
	 */
	public int getAmId() {
		return amId;
	}

	/**
	 * @return An array list containing the state of the activities belonging to this AM.
	 */
	public ArrayList<ActivityState> getAStates() {
		return aStates;
	}

	/**
	 * @return An array list containing the state of the resource types belonging to this AM.
	 */
	public ArrayList<ResourceTypeState> getRtStates() {
		return rtStates;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("AM" + amId);
		if (rtStates.size() > 0) {
			str.append("\r\nRTs:");
			for (ResourceTypeState rt : rtStates)
				str.append("\r\n\t" + rt);
		}
		if (aStates.size() > 0) {
			str.append("\r\nActs:");
			for (ActivityState a : aStates)
				str.append("\r\n\t" + a);
		}
		return str.toString();
	}
}
