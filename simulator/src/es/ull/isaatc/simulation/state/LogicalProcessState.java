/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;

/**
 * Stores the state of a logical process (LP). The state of a LP consists on the state of its activity
 * managers, and the events contained in the waiting queue.
 * @author Iván Castilla Rodríguez
 *
 */
public class LogicalProcessState implements State {
	/** Possible types for the events stored in this LP */
	public enum EventType {FINALIZEACT, ROLOFF};
	/** This LP's identifier */
	protected int lpId;
	/** A list containing the state of the activity managers belonging to this LP */
	protected ArrayList<ActivityManagerState> amStates;
	/** Events in the waiting queue */
	protected ArrayList<EventEntry> waitQueue;
	
	/**
	 * @param lpId This LP's identifier
	 */
	public LogicalProcessState(int lpId) {
		this.lpId = lpId;
		amStates = new ArrayList<ActivityManagerState>();
		waitQueue = new ArrayList<EventEntry>();
	}

	/**
	 * Includes the state of an activity manager in the corresponding list.
	 * @param amState The state of an activity manager
	 */
	public void add(ActivityManagerState amState) {
		amStates.add(amState);
	}

	/**
	 * Adds an event from the waiting list.
	 * @param type Type of the event
	 * @param id Identifier of the element that owns the event
	 * @param ts Timestamp of the event
	 * @param value Value associated to the event
	 */
	public void add(EventType type, int id, double ts, int value) {
		waitQueue.add(new EventEntry(type, id, ts, value));
	}
	
	/**
	 * @return A list containing the states of the activity managers belonging to this LP .
	 */
	public ArrayList<ActivityManagerState> getAmStates() {
		return amStates;
	}

	/**
	 * @return A list containing the events in the waiting queue of this LP.
	 */
	public ArrayList<EventEntry> getWaitQueue() {
		return waitQueue;
	}

	/**
	 * @return This LP's identifier.
	 */
	public int getLpId() {
		return lpId;
	}
	
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("LP" + lpId);
		if (waitQueue.size() > 0)
			str.append("\r\nWAIT QUEUE:");
		for (EventEntry ee : waitQueue)
			str.append("/t[" + ee + "]");
		for (ActivityManagerState ams : amStates)
			str.append("\r\n" + ams);
		return str.toString();
	}
	
	/**
	 * An event from the waiting list.
	 * @author Iván Castilla Rodríguez
	 */
	public class EventEntry {
		/** Type of the event */
		EventType type;
		/** Identifier of the element that owns the event */
		int id;
		/** Timestamp of the event */
		double ts;
		/** Value associated to the event */ 
		int value;
		
		/**
		 * @param type Type of the event
		 * @param id Identifier of the element that owns the event
		 * @param ts Timestamp of the event
		 * @param value Value associated to the event
		 */
		public EventEntry(EventType type, int id, double ts, int value) {
			this.type = type;
			this.id = id;
			this.ts = ts;
			this.value = value;
		}

		/**
		 * @return Type of the event.
		 */
		public EventType getType() {
			return type;
		}

		/**
		 * @return Identifier of the element that owns the event.
		 */
		public int getId() {
			return id;
		}

		/**
		 * @return Timestamp of the event.
		 */
		public double getTs() {
			return ts;
		}

		/**
		 * @return Value associated to the event.
		 */
		public int getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return "EV-" + type + "(" + id + ")\t" + ts + "\t" + value;
		}
	}
}
