/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ResourceTypeState implements State {
	protected int rtId;
	protected ArrayList<ResourceListEntry> availableResourceQueue;

	/**
	 * @param rtId
	 */
	public ResourceTypeState(int rtId) {
		this.rtId = rtId;
		availableResourceQueue = new ArrayList<ResourceListEntry>();
	}

	/**
	 * @return Returns the availableResourceQueue.
	 */
	public ArrayList<ResourceListEntry> getAvailableResourceQueue() {
		return availableResourceQueue;
	}

	/**
	 * @return Returns the rtId.
	 */
	public int getRtId() {
		return rtId;
	}

	public void add(int resId, int count) {
		availableResourceQueue.add(new ResourceListEntry(resId, count));
	}
	
	public class ResourceListEntry {
		int resId;
		int count;
		
		/**
		 * @param resId
		 * @param count
		 */
		public ResourceListEntry(int resId, int count) {
			this.resId = resId;
			this.count = count;
		}
		
		/**
		 * @return Returns the count.
		 */
		public int getCount() {
			return count;
		}
		/**
		 * @return Returns the resId.
		 */
		public int getResId() {
			return resId;
		}
	}
}
