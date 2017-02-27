/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.util.Prioritizable;

/**
 * @author Iv�n Castilla
 *
 */
public class Element extends EventSource implements Prioritizable {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Presential work thread which the element is currently carrying out */
	protected FlowExecutor current = null;
	/** Main execution thread */
	protected final FlowExecutor mainThread;
	private ElementEngine engine = null;
	
	public Element(Model model, ElementType elementType, InitializerFlow initialFlow) {
		super(model, model.getNewElementId(), "E");
		this.elementType = elementType;
		this.initialFlow = initialFlow;
		mainThread = FlowExecutor.getInstanceMainFlowExecutor(this);
	}
	
	/**
	 * Returns the corresponding type of the element.
	 * @return the corresponding type of the element
	 */
	public ElementType getType() {
		return elementType;
	}
	
	/**
	 * Returns the associated {@link es.ull.iis.simulation.model.flow.InitializerFlow Flow}.
	 * @return the associated {@link es.ull.iis.simulation.model.flow.InitializerFlow Flow}
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
	 * Notifies a new work thread is waiting in an activity queue.
	 * @param wt Work thread waiting in queue.
	 */
	public void incInQueue(FlowExecutor fe) {
		engine.incInQueue(fe);
	}
	
	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	public void decInQueue(FlowExecutor fe) {
		engine.decInQueue(fe);
	}
	
	/**
	 * If the element is currently performing an activity, returns the work
	 * thread used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work thread corresponding to the current presential activity being
	 * performed by this element.
	 */
	public FlowExecutor getCurrent() {
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
	public void setCurrent(FlowExecutor current) {
		this.current = current;
		if (current == null) {
			engine.notifyAvailableElement();
		}
	}

	public void initializeElementVars(TreeMap<String, Object> varList) {
		for (Entry<String, Object> entry : varList.entrySet()) {
			final String name = entry.getKey();
			final Object value = entry.getValue();
			if (value instanceof Number) {
				this.putVar(name, ((Number)value).doubleValue());
			}
			else {
				if (value instanceof Boolean) {
					this.putVar(name, ((Boolean)value).booleanValue());
				}
				else if (value instanceof EnumVariable) {
						this.putVar(name, ((EnumVariable)value));
				}
				else if (value instanceof Character) {
					this.putVar(name, ((Character)value).charValue());
				}
			}
		}
	}
	
	/**
	 * Initializes the element by requesting the <code>initialFlow</code>. If there's no initial flow
	 * the element finishes immediately.
	 */
	@Override
	public DiscreteEvent onCreate(long ts) {
		model.notifyInfo(new ElementInfo(model, this, elementType, ElementInfo.Type.START, model.getSimulationEngine().getTs()));
		if (initialFlow != null) {
			return (new RequestFlowEvent(model.getSimulationEngine().getTs(), initialFlow, mainThread.getInstanceDescendantFlowExecutor(initialFlow)));
		}
		else
			return onDestroy();
	}

	@Override
	public DiscreteEvent onDestroy() {
		model.notifyInfo(new ElementInfo(model, this, elementType, ElementInfo.Type.FINISH, model.getSimulationEngine().getTs()));
		return new DefaultFinalizeEvent();
	}

	public void addRequestEvent(Flow f, FlowExecutor fe) {
		model.getSimulationEngine().addEvent(new RequestFlowEvent(model.getSimulationEngine().getTs(), f, fe));
	}
	
	public void addFinishEvent(long ts, TaskFlow f, FlowExecutor fe) {
		model.getSimulationEngine().addEvent(new FinishFlowEvent(ts, f, fe));
	}
	
	/**
	 * Requests a flow.
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class RequestFlowEvent extends DiscreteEvent {
		/** The work thread that executes the request */
		private final FlowExecutor fe;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, FlowExecutor fe) {
			super(ts);
			this.fe = fe;
			this.f = f;
		}		

		@Override
		public void event() {
			fe.setCurrentFlow(f);
			f.request(fe);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class FinishFlowEvent extends DiscreteEvent {
		/** The work thread that executes the finish */
		private final FlowExecutor fe;
		/** The flow to be finished */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, FlowExecutor fe) {
			super(ts);
			this.fe = fe;
			this.f = f;
		}		

		@Override
		public void event() {
			f.finish(fe);
		}
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getElementEngineInstance(this);
	}
	
}
