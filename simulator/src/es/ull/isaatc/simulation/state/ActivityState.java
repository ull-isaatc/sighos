/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of an activity. The state of an activity consists on the queue of single flows
 * waiting to be executed.
 * @author Iván Castilla Rodríguez
 *
 */
public class ActivityState implements State {
	/** This activity's identifier */ 
	protected int actId;
	/** Waiting element queue */
	protected ArrayList<ActivityQueueEntry> queue;
	
	/**
	 * @param actId This activity's identifier
	 */
	public ActivityState(int actId) {
		this.actId = actId;
		queue = new ArrayList<ActivityQueueEntry>();
	}

	/**
	 * @return This activity's identifier.
	 */
	public int getActId() {
		return actId;
	}

	/**
	 * @return An array list containing the queue of single flows waiting to be executed.
	 */
	public ArrayList<ActivityQueueEntry> getQueue() {
		return queue;
	}
	
	/**
	 * Adds a new single flow to the waiting queue of this activity state. 
	 * @param flowId The single flow's identifier
	 * @param elemId The element's identifier
	 */
	public void add(int flowId, int elemId) {
		queue.add(new ActivityQueueEntry(flowId, elemId));
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("A" + actId);
		if (queue.size() > 0)
			str.append(". Queue:");
		for (ActivityQueueEntry entry : queue)
			str.append(" " + entry);
		return str.toString();
	}

	/**
	 * The content of the activity queue.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityQueueEntry {
		/** The single flow's identifier */
		int flowId;
		/** The element's identifier */
		int elemId;
		
		/**
		 * @param flowId The single flow's identifier
		 * @param elemId The element's identifier
		 */
		public ActivityQueueEntry(int flowId, int elemId) {
			this.flowId = flowId;
			this.elemId = elemId;
		}
		
		/**
		 * @return The element's identifier.
		 */
		public int getElemId() {
			return elemId;
		}
		
		/**
		 * @return The single flow's identifier.
		 */
		public int getFlowId() {
			return flowId;
		}

		@Override
		public String toString() {
			return "E" + elemId + "(" + flowId + ")"; 
		}
	}
}
