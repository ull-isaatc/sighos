/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityManagerState implements State {
	protected int amId;
	protected ArrayList<ActivityState> aStates;
	protected ArrayList<ResourceTypeState> rtStates;
	
	/**
	 * @param amId
	 */
	public ActivityManagerState(int amId) {
		this.amId = amId;
		aStates = new ArrayList<ActivityState>();
		rtStates = new ArrayList<ResourceTypeState>();
	}
	
	public void add(ActivityState aState) {
		aStates.add(aState);
	}

	public void add(ResourceTypeState rtState) {
		rtStates.add(rtState);
	}

	/**
	 * @return Returns the amId.
	 */
	public int getAmId() {
		return amId;
	}

	/**
	 * @return Returns the aStates.
	 */
	public ArrayList<ActivityState> getAStates() {
		return aStates;
	}

	/**
	 * @return Returns the rtStates.
	 */
	public ArrayList<ResourceTypeState> getRtStates() {
		return rtStates;
	}
	
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
