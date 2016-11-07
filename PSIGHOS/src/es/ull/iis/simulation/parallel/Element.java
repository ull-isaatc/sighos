package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.parallel.flow.Flow;
import es.ull.iis.simulation.parallel.flow.InitializerFlow;
import es.ull.iis.simulation.parallel.flow.TaskFlow;
import es.ull.iis.simulation.variable.EnumVariable;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * TODO Comment
 * @author Iv�n Castilla Rodr�guez
 */
public class Element extends BasicElement implements es.ull.iis.simulation.core.Element {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<WorkItem> inQueue = new ArrayList<WorkItem>();
	/** Presential work item which the element is currently carrying out */
	protected WorkItem current = null;
	/** Main execution thread */
	protected final WorkThread wThread;
	/** A structure to protect access to shared flows */
	protected final Map<Flow, AtomicBoolean> protectedFlows;
	
	/**
	 * Creates a new element.
	 * @param id Element's identifier
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public Element(Simulation simul, ElementType et, InitializerFlow flow) {
		super(simul.getNextElementId(), simul);
		this.elementType = et;
		this.initialFlow = flow;
		wThread = WorkThread.getInstanceMainWorkThread(this);
		protectedFlows = new HashMap<Flow, AtomicBoolean>();
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
			wThread.getInstanceDescendantWorkThread().requestFlow(initialFlow);
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
			for (WorkItem wi : inQueue)
				if (!wi.getActivity().isNonPresential())
					wi.getActivity().getManager().notifyElement(wi);
		}		
	}
	
	/**
	 * Acquires a semaphore associated to a specific flow
	 * @param flow The flow to be requested
	 */
	public void waitProtectedFlow(Flow flow) {
		waitSemaphore();
		if (!protectedFlows.containsKey(flow)) {
			protectedFlows.put(flow, new AtomicBoolean(true));
			signalSemaphore();
		}
		else {
			signalSemaphore();
			final AtomicBoolean localBool = protectedFlows.get(flow);
			while (!localBool.compareAndSet(false, true));
		}
	}
	
	/**
	 * Releases a semaphore associated to a specific flow
	 * @param flow The flow to be requested
	 */
	public void signalProtectedFlow(Flow flow) {
		protectedFlows.get(flow).set(false);
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
	
	protected void addDelayedRequestEvent(Flow f, WorkThread wThread) {
		addEvent(new DelayedRequestFlowEvent(ts + 1, f, wThread));
	}
	
	/**
	 * Requests a flow a time unit later than the current timestamp. This event is used by
	 * {@link FlowDrivenActivity}.
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class DelayedRequestFlowEvent extends BasicElement.DiscreteEvent {
		/** The work thread that executes the request */
		private final WorkThread eThread;
		/** The flow to be requested */
		private final Flow f;

		public DelayedRequestFlowEvent(long ts, Flow f, WorkThread eThread) {
			super(ts);
			this.eThread = eThread;
			this.f = f;
		}		

		public void event() {
			eThread.requestFlow(f);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iv�n Castilla Rodr�guez
	 */
	public final class FinishFlowEvent extends BasicElement.DiscreteEvent {
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
}
