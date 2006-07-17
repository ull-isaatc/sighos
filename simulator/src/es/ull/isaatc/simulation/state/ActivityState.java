/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityState implements State {
	protected int actId;
	/** Waiting element queue */
	protected ArrayList<ActivityQueueEntry> queue;
	
	/**
	 * @param actId
	 * @param elemId
	 */
	public ActivityState(int actId) {
		this.actId = actId;
		queue = new ArrayList<ActivityQueueEntry>();
	}

	/**
	 * @return Returns the actId.
	 */
	public int getActId() {
		return actId;
	}

	/**
	 * @return Returns the list.
	 */
	public ArrayList<ActivityQueueEntry> getQueue() {
		return queue;
	}
	
	public void add(int flowId, int elemId) {
		queue.add(new ActivityQueueEntry(flowId, elemId));
	}

	public String toString() {
		StringBuffer str = new StringBuffer("A" + actId);
		if (queue.size() > 0)
			str.append(". Queue:");
		for (ActivityQueueEntry entry : queue)
			str.append(" " + entry);
		return str.toString();
	}
	
	public class ActivityQueueEntry {
		int flowId;
		int elemId;
		/**
		 * @param flowId
		 * @param elemId
		 */
		public ActivityQueueEntry(int flowId, int elemId) {
			this.flowId = flowId;
			this.elemId = elemId;
		}
		/**
		 * @return Returns the elemId.
		 */
		public int getElemId() {
			return elemId;
		}
		/**
		 * @return Returns the flowId.
		 */
		public int getFlowId() {
			return flowId;
		}

		public String toString() {
			return "E" + elemId + "(" + flowId + ")"; 
		}
	}
}
