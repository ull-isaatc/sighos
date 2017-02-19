package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.sequential.flow.FlowBehaviour;
import es.ull.iis.simulation.variable.EnumVariable;

/**
 * Represents case instances that make use of activity flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class Element extends EventSource implements es.ull.iis.simulation.model.Element {
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
	private final FlowBehaviour flowHandler = new FlowBehaviour(this);
	
	/**
	 * Creates a new element.
	 * @param simul Simulation object
	 * @param et Element type this element belongs to
	 * @param flow First step of this element's flow
	 */
	public Element(Simulation simul, ElementType et, InitializerFlow flow) {
		super(simul.getNextElementId(), simul, "E");
		this.elementType = et;
		inQueue = new ArrayList<WorkThread>();
		this.initialFlow = flow;
		mainThread = WorkThread.getInstanceMainWorkThread(this);
	}

	/**
	 * Returns the element type this element belongs to.
	 * @return the elementType
	 */
	public es.ull.iis.simulation.model.ElementType getType() {
		return elementType.getModelET();
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
	 * being performed by this element.Creates the events to notify the activities that this element is now
	 * available. All the activities this element is in their queues are notified. 
	 * @param current The work thread corresponding to the current presential activity 
	 * being performed by this element. A null value indicates that the element has 
	 * finished performing the activity.
	 */
	public void setCurrent(WorkThread current) {
		this.current = current;
		if (current == null) {
			// Checks if there are pending activities that haven't noticed the
			// element availability
			for (int i = 0; (current == null) && (i < inQueue.size()); i++) {
				final WorkThread wThread = inQueue.get(i);
	            final RequestResourcesFlow act = (RequestResourcesFlow) wThread.getCurrentFlow();

				if (isDebugEnabled())
					debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
				wThread.availableElement(simul.getRequestResource(act));
			}
		}
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
	public DiscreteEvent onCreate(long ts) {
		simul.getInfoHandler().notifyInfo(new ElementInfo(simul, this, elementType.getModelET(), ElementInfo.Type.START, this.getTs()));
		simul.addActiveElement(this);
		if (initialFlow != null) {
			return new RequestFlowEvent(ts, initialFlow, mainThread.getInstanceDescendantWorkThread(initialFlow));
		}
		else
			return onDestroy();
	}

	@Override
	public DiscreteEvent onDestroy() {
		simul.getInfoHandler().notifyInfo(new ElementInfo(simul, this, elementType.getModelET(), ElementInfo.Type.FINISH, this.getTs()));
		simul.removeActiveElement(this);
		return new DefaultFinalizeEvent();
	}

	/**
	 * @return the flowHandler
	 */
	public FlowBehaviour getFlowHandler() {
		return flowHandler;
	}

	/**
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	public void incInQueue(WorkThread wt) {
			inQueue.add(wt);
	}

	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	public void decInQueue(WorkThread wt) {
			inQueue.remove(wt);
	}

	public void addRequestEvent(Flow f, WorkThread wThread) {
		addEvent(new RequestFlowEvent(getTs(), f, wThread));
	}
	
	public void addFinishEvent(long ts, WorkThread wThread) {
		addEvent(new FinishFlowEvent(ts, wThread));
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
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestFlowEvent extends DiscreteEvent {
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
			flowHandler.request(wThread);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iván Castilla Rodríguez
	 */
	public class FinishFlowEvent extends DiscreteEvent {
		/** The work thread that executes the finish */
		private final WorkThread wThread;

		public FinishFlowEvent(long ts, WorkThread wThread) {
			super(ts);
			this.wThread = wThread;
		}		

		@Override
		public void event() {
			flowHandler.finish(wThread);
		}
	}
	
}
