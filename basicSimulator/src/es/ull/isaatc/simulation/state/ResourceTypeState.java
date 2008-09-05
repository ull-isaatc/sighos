/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of a resource type (RT). The state of an RT consists on the list
 * of available resources.
 * @author Iván Castilla Rodríguez
 */
public class ResourceTypeState implements State {
	private static final long serialVersionUID = -4096228024023672393L;
	/** This RT's identifier */
	protected int rtId;
	/** The list of available resources */
	protected ArrayList<ResourceListEntry> availableResourceQueue;

	/**
	 * @param rtId This RT's identifier
	 */
	public ResourceTypeState(int rtId) {
		this.rtId = rtId;
		availableResourceQueue = new ArrayList<ResourceListEntry>();
	}

	/**
	 * @return The list of available resources.
	 */
	public ArrayList<ResourceListEntry> getAvailableResourceQueue() {
		return availableResourceQueue;
	}

	/**
	 * @return This resource type's identifier.
	 */
	public int getRtId() {
		return rtId;
	}

	/**
	 * Adds an available resource to the list. Includes the count indicating how many
	 * times this resource has become available.
	 * @param resId
	 * @param count
	 */
	public void add(int resId, int count) {
		availableResourceQueue.add(new ResourceListEntry(resId, count));
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("RT" + rtId);
		if (availableResourceQueue.size() > 0)
			str.append(". Resource list:");
		for (ResourceListEntry entry : availableResourceQueue)
			str.append(" " + entry);
		return str.toString();
	}
	
	/**
	 * The content of the available resource list.
	 * @author Iván Castilla Rodríguez
	 */
	public class ResourceListEntry {
		/** The resource's identifier */
		int resId;
		/** How many times this resource has become available for this resource type */
		int count;
		
		/**
		 * @param resId The resource's identifier
		 * @param count How many times this resource has become available for this resource type
		 */
		public ResourceListEntry(int resId, int count) {
			this.resId = resId;
			this.count = count;
		}
		
		/**
		 * @return How many times this resource has become available for this resource type.
		 */
		public int getCount() {
			return count;
		}
		/**
		 * @return The resource's identifier.
		 */
		public int getResId() {
			return resId;
		}

		@Override
		public String toString() {
			return "R" + resId + "(" + count + ")"; 
		}
	}
}
