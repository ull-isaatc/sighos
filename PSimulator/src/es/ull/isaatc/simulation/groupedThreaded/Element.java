package es.ull.isaatc.simulation.groupedThreaded;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.variable.EnumVariable;
import es.ull.isaatc.simulation.groupedThreaded.flow.Flow;
import es.ull.isaatc.simulation.groupedThreaded.flow.InitializerFlow;
import es.ull.isaatc.simulation.groupedThreaded.flow.TaskFlow;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class Element extends BasicElement implements es.ull.isaatc.simulation.common.Element {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<WorkItem> inQueue;
	/** Presential work item which the element is currently carrying out */
	protected WorkItem current = null;
	/** Main execution thread */
	protected final WorkThread wThread;
	
	/**
	 * Creates a new element.
	 * @param id Element's identifier
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public Element(int id, Simulation simul, ElementType et, InitializerFlow flow) {
		super(id, simul);
		this.elementType = et;
		inQueue = new ArrayList<WorkItem>();
		this.initialFlow = flow;
		wThread = WorkThread.getInstanceMainWorkThread(this);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "E";
	}

	/**
	 * Returns the element type this element belongs to.
	 * @return the elementType
	 */
	public ElementType getType() {
		return elementType;
	}

	/**
	 * If the element is currently performing an activity, returns the work
	 * item used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work item corresponding to the current presential activity being
	 * performed by this element.
	 */
	public WorkItem getCurrent() {
		return current;
	}

	/**
	 * Sets the work item corresponding to the current presential activity 
	 * being performed by this element. 
	 * @param current The work item corresponding to the current presential activity 
	 * being performed by this element. A null value indicates that the element has 
	 * finished performing the activity.
	 */
	protected void setCurrent(WorkItem current) {
		this.current = current;
	}

	/**
	 * Returns the first step of this element's flow.
	 * @return the first step of this element's flow.
	 */
	public InitializerFlow getFlow() {
		return initialFlow;
	}

	/**
	 * Returns the element's priority, which is the element type's priority.
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return elementType.getPriority();
	}

	/**
	 * Initializes the element by requesting the <code>initialFlow</code>. If there's no initial flow
	 * the element finishes immediately.
	 */
	@Override
	protected void init() {
		simul.getInfoHandler().notifyInfo(new ElementInfo(this.simul, this, ElementInfo.Type.START, this.getTs()));
		simul.addActiveElement(this);
		if (initialFlow != null) {
			addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
		}
		else
			notifyEnd();
	}

	@Override
	protected void end() {
		simul.getInfoHandler().notifyInfo(new ElementInfo(this.simul, this, ElementInfo.Type.FINISH, this.getTs()));
		simul.removeActiveElement(this);
	}

	/**
	 * Notifies a new work item is waiting in an activity queue.
	 * @param wi Work item waiting in queue.
	 */
	protected void incInQueue(WorkItem wi) {
		synchronized(inQueue) {
			inQueue.add(wi);
		}
	}

	/**
	 * Notifies a work item has finished waiting in an activity queue.
	 * @param wi Work item that was waiting in a queue.
	 */
	protected void decInQueue(WorkItem wi) {
		synchronized(inQueue) {
			inQueue.remove(wi);
		}
	}

	/**
	 * Creates the events to notify the activities that this element is now
	 * available. All the activities this element is in their queues are notified.
	 */
	protected void addAvailableElementEvents() {
		synchronized(inQueue) {
			for (int i = 0; (current == null) && (i < inQueue.size()); i++)
				addEvent(new AvailableElementEvent(ts, inQueue.get(i)));
		}		
	}
	
	/**
	 * Adds a new request event.
	 * @param f The flow to be requested
	 * @param wThread The work thread used to request the flow
	 */
	public void addRequestEvent(Flow f, WorkThread wThread) {
		addEvent(new RequestFlowEvent(ts, f, wThread));
	}
	
	public void initializeElementVars(HashMap<String, Object> varList) {
		Iterator<Entry<String, Object>> iter = varList.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Number)
				this.putVar(name, ((Number)value).doubleValue());
			else
				if (value instanceof Boolean)
					this.putVar(name, ((Boolean)value).booleanValue());
				else 
					if (value instanceof EnumVariable)
						this.putVar(name, ((EnumVariable)value));
					else 
						if (value instanceof Character)
							this.putVar(name, ((Character)value).charValue());
				
		}
	}
	
	/**
	 * Requests a flow.
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestFlowEvent extends BasicElement.DiscreteEvent {
		/** The work thread that executes the request */
		private final WorkThread eThread;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, WorkThread eThread) {
			super(ts);
			this.eThread = eThread;
			this.f = f;
		}		

		public void event() {
			f.request(eThread);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iván Castilla Rodríguez
	 */
	public class FinishFlowEvent extends BasicElement.DiscreteEvent {
		/** The work thread that executes the finish */
		private final WorkThread eThread;
		/** The flow previously requested */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, WorkThread eThread) {
			super(ts);
			this.f = f;
			this.eThread = eThread;
		}		

		public void event() {
			f.finish(eThread);
		}
	}
	
	/**
	 * Informs an activity about an available element in its queue.
	 * @author Iván Castilla Rodríguez
	 */
	public class AvailableElementEvent extends BasicElement.DiscreteEvent {
		/** Flow informed of the availability of the element */
		private final WorkItem eThread;

		public AvailableElementEvent(long ts, WorkItem eThread) {
			super(ts);
			this.eThread = eThread;
		}

		public void event() {
			Activity act = eThread.getActivity();
			// Beginning MUTEX access to activity manager
			act.getManager().waitSemaphore();

            waitSemaphore();
			if (isDebugEnabled())
				debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
			// If the element is not performing a presential activity yet
			if (current == null)
				if (act.isFeasible(eThread)) {
		        	signalSemaphore();
					act.carryOut(eThread);
					act.queueRemove(eThread);
				}
				else {
		        	signalSemaphore();
				}
			else {
	        	signalSemaphore();
			}
    		
			// Ending MUTEX access to activity manager
			act.getManager().signalSemaphore();
		}
	}
}
