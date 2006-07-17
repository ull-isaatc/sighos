/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LogicalProcessState implements State {
	public enum EventType {FINALIZEACT, ROLOFF};
	protected int lpId;
	protected ArrayList<ActivityManagerState> amStates;
	/** Events in the waiting queue */
	protected ArrayList<EventEntry> waitQueue;
	
	public LogicalProcessState(int lpId) {
		this.lpId = lpId;
		amStates = new ArrayList<ActivityManagerState>();
		waitQueue = new ArrayList<EventEntry>();
	}
	
	public void add(ActivityManagerState amState) {
		amStates.add(amState);
	}

	public void add(EventType type, int id, double ts, int value) {
		waitQueue.add(new EventEntry(type, id, ts, value));
	}
	
	/**
	 * @return Returns the amStates.
	 */
	public ArrayList<ActivityManagerState> getAmStates() {
		return amStates;
	}

	/**
	 * @return Returns the waitQueue.
	 */
	public ArrayList<EventEntry> getWaitQueue() {
		return waitQueue;
	}

	/**
	 * @return Returns the lpId.
	 */
	public int getLpId() {
		return lpId;
	}
	
	public class EventEntry {
		EventType type;
		int id;
		double ts;
		int value;
		
		/**
		 * @param type
		 * @param id
		 * @param ts
		 * @param value
		 */
		public EventEntry(EventType type, int id, double ts, int value) {
			this.type = type;
			this.id = id;
			this.ts = ts;
			this.value = value;
		}

		/**
		 * @return Returns the type.
		 */
		public EventType getType() {
			return type;
		}

		/**
		 * @return Returns the id.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return Returns the ts.
		 */
		public double getTs() {
			return ts;
		}

		/**
		 * @return Returns the value.
		 */
		public int getValue() {
			return value;
		}
	}
}
