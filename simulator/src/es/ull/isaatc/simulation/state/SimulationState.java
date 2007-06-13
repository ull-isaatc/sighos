/**
 * 
 */
package es.ull.isaatc.simulation.state;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Stores the state of a simulation. The state of a simulation consists on the state
 * of its logical processes, elements and resources.
 * @author Iván Castilla Rodríguez
 */
public class SimulationState implements State {
	private static final long serialVersionUID = 6461585178077450859L;
	/** The state of the elements belonging to this simulation */
	protected ArrayList<ElementState> elemStates;
	/** The state of the resources belonging to this simulation */
	protected ArrayList<ResourceState> resStates;
	/** State of the activities belonging to this simulation */
	protected ArrayList<ActivityState> aStates;
	/** State of the resource types belonging to this simulation */
	protected ArrayList<ResourceTypeState> rtStates;
	/** Last element created in this simulation */
	protected int lastElemId;
	/** Last single flow created in this simulation */
	protected int lastSFId;
	/** Final timestamp of this simulation */
	protected double endTs;
	/** Possible types for the events stored in this LP */
	public enum EventType {FINALIZEACT, ROLOFF};
	/** Events in the waiting queue */
	protected ArrayList<EventEntry> waitQueue;
	/** Single flows which are waiting to be executed in an activity queue, ordered by their 
	 * arrival timestamp.
	 */
	protected PriorityQueue<SFEntry> sfQueue;
	
	/**
	 * @param lastElemId Last element created in this simulation
	 * @param lastSFId Last single flow created in this simulation
	 * @param endTs Final timestamp of this simulation
	 */
	public SimulationState(int lastElemId, int lastSFId, double endTs) {
		elemStates = new ArrayList<ElementState>();
		resStates = new ArrayList<ResourceState>();
		aStates = new ArrayList<ActivityState>();
		rtStates = new ArrayList<ResourceTypeState>();
		waitQueue = new ArrayList<EventEntry>();
		sfQueue = new PriorityQueue<SFEntry>();
		this.lastElemId = lastElemId;
		this.lastSFId = lastSFId;
		this.endTs = endTs;
	}
	
	/**
	 * Includes the state of an element
	 * @param eState The state of an element belonging to this simulation
	 */
	public void add(ElementState eState) {
		elemStates.add(eState);
	}

	/**
	 * Includes the state of a resource
	 * @param rState The state of a resource belonging to this simulation
	 */
	public void add(ResourceState rState) {
		resStates.add(rState);
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
	 * Adds a single flow waiting in an activity queue.
	 * @param flowId The single flow's identifier
	 * @param elemId The element's identifier
	 * @param arrivalTs The single flow's arrival timestamp
	 * @param order The relative order between single flow's arrivals
	 */
	public void add(int flowId, int elemId, double arrivalTs, int order) {
		sfQueue.add(new SFEntry(flowId, elemId, arrivalTs, order));
	}
	
	/**
	 * @return The state of the elements belonging to this simulation.
	 */
	public ArrayList<ElementState> getElemStates() {
		return elemStates;
	}

	/**
	 * @return The state of the resources belonging to this simulation.
	 */
	public ArrayList<ResourceState> getResStates() {
		return resStates;
	}

	/**
	 * @return An array list containing the state of the activities belonging to this simulation.
	 */
	public ArrayList<ActivityState> getAStates() {
		return aStates;
	}

	/**
	 * @return An array list containing the state of the resource types belonging to this simulation.
	 */
	public ArrayList<ResourceTypeState> getRtStates() {
		return rtStates;
	}
	
	/**
	 * @return Final timestamp of this simulation.
	 */
	public double getEndTs() {
		return endTs;
	}

	/**
	 * @return Last element created in this simulation.
	 */
	public int getLastElemId() {
		return lastElemId;
	}

	/**
	 * @return Last single flow created in this simulation.
	 */
	public int getLastSFId() {
		return lastSFId;
	}

	/**
	 * @return A list containing the events in the waiting queue of this LP.
	 */
	public ArrayList<EventEntry> getWaitQueue() {
		return waitQueue;
	}

	/**
	 * Returns an ordered list of single flows waiting to be executed in an activity queue, 
	 * ordered by their arrival timestamp.
	 * @return an ordered list of single flows waiting to be executed in an activity queue.
	 */
	public PriorityQueue<SFEntry> getSfQueue() {
		return sfQueue;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("SIMULATION(" + endTs + ")\tLast E: " + lastElemId + "\tLast SF: " + lastSFId + "\r\n");
		for (ElementState es : elemStates)
			str.append(es + "\r\n");
		for (ResourceState rs : resStates)
			str.append(rs + "\r\n");
		for (ResourceTypeState rt : rtStates)
			str.append(rt + "\r\n");
		for (ActivityState a : aStates)
			str.append(a + "\r\n");
		if (waitQueue.size() > 0)
			str.append("\r\nWAIT QUEUE:");
		for (EventEntry ee : waitQueue)
			str.append("/t[" + ee + "]");
		if (sfQueue.size() > 0)
			str.append("\r\nSF QUEUE:");
		for (SFEntry sf : sfQueue)
			str.append("/t[" + sf + "]");
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
	
	public class SFEntry implements Comparable<SFEntry> {
		/** The single flow's identifier */
		int flowId;
		/** The element's identifier */
		int elemId;
		/** The single flow's arrival timestamp */
		double arrivalTs;
		/** The relative order between single flow's arrivals */
		int order;
		
		/**
		 * @param flowId The single flow's identifier
		 * @param elemId The element's identifier
		 * @param arrivalTs The single flow's arrival timestamp
		 * @param order The relative order between single flow's arrivals
		 */
		public SFEntry(int flowId, int elemId, double arrivalTs, int order) {
			this.flowId = flowId;
			this.elemId = elemId;
			this.arrivalTs = arrivalTs;
			this.order = order;
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

		/**
		 * @return the arrivalTs
		 */
		public double getArrivalTs() {
			return arrivalTs;
		}

		/**
		 * @return the order
		 */
		public int getOrder() {
			return order;
		}

		@Override
		public String toString() {
			return "E" + elemId + "(" + flowId + ") " + arrivalTs + "(" + order + ")"; 
		}

		public int compareTo(SFEntry o) {
			if (arrivalTs > o.arrivalTs)
				return 1;
			if (arrivalTs < o.arrivalTs)
				return -1;
			if (order > o.order)
				return 1;
			if (order < o.order)
				return -1;
			return 0;
		}		
	}
}
