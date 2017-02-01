package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.sequential.flow.Flow;
import es.ull.iis.simulation.sequential.flow.InitializerFlow;
import es.ull.iis.simulation.sequential.flow.TaskFlow;
import es.ull.iis.simulation.variable.EnumVariable;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iv�n Castilla Rodr�guez
 */
public class Element extends BasicElement implements es.ull.iis.simulation.core.Element {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Activity queues in which this element is. This list is used to notify the activities
	 * when the element becomes available. */
	protected final ArrayList<WorkThread> inQueue;
	/** Presential work thread which the element is currently carrying out */
	protected WorkThread current = null;
	/** Main execution thread */
	protected final WorkThread mainThread;
	
	/**
	 * Creates a new element.
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public Element(Simulation simul, ElementType et, InitializerFlow flow) {
		super(simul.getNextElementId(), simul);
		this.elementType = et;
		inQueue = new ArrayList<WorkThread>();
		this.initialFlow = flow;
		mainThread = WorkThread.getInstanceMainWorkThread(this);
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
	 * thread used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work thread corresponding to the current presential activity being
	 * performed by this element.
	 */
	public WorkThread getCurrent() {
		return current;
	}

	/**
	 * Sets the work thread corresponding to the current presential activity 
	 * being performed by this element. 
	 * @param current The work thread corresponding to the current presential activity 
	 * being performed by this element. A null value indicates that the element has 
	 * finished performing the activity.
	 */
	protected void setCurrent(WorkThread current) {
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
			addRequestEvent(initialFlow, mainThread.getInstanceDescendantWorkThread(initialFlow));
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
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	protected void incInQueue(WorkThread wt) {
			inQueue.add(wt);
	}

	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	protected void decInQueue(WorkThread wt) {
			inQueue.remove(wt);
	}

	/**
	 * Creates the events to notify the activities that this element is now
	 * available. All the activities this element is in their queues are notified.
	 */
	protected void addAvailableElementEvents() {
		for (int i = 0; (current == null) && (i < inQueue.size()); i++)
			addEvent(new AvailableElementEvent(ts, inQueue.get(i)));
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
			Entry<String, Object> entry = iter.next();
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
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class RequestFlowEvent extends BasicElement.DiscreteEvent {
		/** The work thread that executes the request */
		private final WorkThread wThread;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, WorkThread wThread) {
			super(ts);
			this.wThread = wThread;
			this.f = f;
		}		

		@Override
		public void event() {
			wThread.setCurrentFlow(f);
			f.request(wThread);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class FinishFlowEvent extends BasicElement.DiscreteEvent {
		/** The work thread that executes the finish */
		private final WorkThread wThread;
		/** The flow previously requested */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, WorkThread wThread) {
			super(ts);
			this.f = f;
			this.wThread = wThread;
		}		

		@Override
		public void event() {
			f.finish(wThread);
		}
	}
	
	/**
	 * Informs an activity about an available element in its queue.
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class AvailableElementEvent extends BasicElement.DiscreteEvent {
		/** Flow informed of the availability of the element */
		private final WorkThread wThread;

		public AvailableElementEvent(long ts, WorkThread wThread) {
			super(ts);
			this.wThread = wThread;
		}

		@Override
		public void event() {
			BasicStep act = wThread.getBasicStep();

			if (isDebugEnabled())
				debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
			// If the element is not performing a presential activity yet
			if (current == null) {
				ArrayDeque<Resource> solution = act.isFeasible(wThread);
				if (solution != null) {
					act.carryOut(wThread, solution);
					act.queueRemove(wThread);
				}
			}
		}
	}
}
