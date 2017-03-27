/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.Map.Entry;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.engine.ElementEngine;
import es.ull.iis.simulation.model.engine.SimulationEngine;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.util.Prioritizable;

/**
 * @author Iván Castilla
 *
 */
public class Element extends VariableStoreModelObject implements Prioritizable, EventSource {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	/** Exclusive instance which the element is currently carrying out */
	protected ElementInstance current = null;
	/** Main element instance */
	protected ElementInstance mainInstance = null;
	/** The engine that executes specific behavior of the element */
	private ElementEngine engine;
	
	public Element(Simulation simul, ElementType elementType, InitializerFlow initialFlow) {
		super(simul, simul.getNewElementId(), "E");
		this.elementType = elementType;
		this.initialFlow = initialFlow;
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
	public void incInQueue(ElementInstance fe) {
		engine.incInQueue(fe);
	}
	
	/**
	 * Notifies a work thread has finished waiting in an activity queue.
	 * @param wt Work thread that was waiting in a queue.
	 */
	public void decInQueue(ElementInstance fe) {
		engine.decInQueue(fe);
	}
	
	/**
	 * If the element is currently performing an activity, returns the work
	 * thread used by the element. If the element is not performing any presential 
	 * activity, returns null.
	 * @return The work thread corresponding to the current presential activity being
	 * performed by this element.
	 */
	public ElementInstance getCurrent() {
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
	public void setCurrent(ElementInstance current) {
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
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.START, simul.getSimulationEngine().getTs()));
		if (initialFlow != null) {
			mainInstance = ElementInstance.getMainElementInstance(this);
			return (new RequestFlowEvent(simul.getSimulationEngine().getTs(), initialFlow, mainInstance.getDescendantElementInstance(initialFlow)));
		}
		else
			return onDestroy(ts);
	}

	@Override
	public DiscreteEvent onDestroy(long ts) {
		simul.notifyInfo(new ElementInfo(simul, this, elementType, ElementInfo.Type.FINISH, simul.getSimulationEngine().getTs()));
		return new DiscreteEvent.DefaultFinalizeEvent(this, ts);
		// TODO: Check if the following action should be performed within the event
		// simul.removeActiveElement(this);
	}

    /**
     * Informs the element that it must finish its execution. Thus, a FinalizeEvent is
     * created.
     */
    public void notifyEnd() {
    	engine.notifyEnd();
    }
    
	public void addRequestEvent(Flow f, ElementInstance fe) {
		simul.addEvent(new RequestFlowEvent(simul.getSimulationEngine().getTs(), f, fe));
	}
	
	public void addFinishEvent(long ts, TaskFlow f, ElementInstance fe) {
		simul.addEvent(new FinishFlowEvent(ts, f, fe));
	}
	
	/**
	 * Requests a flow.
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestFlowEvent extends DiscreteEvent {
		/** The work thread that executes the request */
		private final ElementInstance fe;
		/** The flow to be requested */
		private final Flow f;

		public RequestFlowEvent(long ts, Flow f, ElementInstance fe) {
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
	 * @author Iván Castilla Rodríguez
	 */
	public class FinishFlowEvent extends DiscreteEvent {
		/** The work thread that executes the finish */
		private final ElementInstance fe;
		/** The flow to be finished */
		private final TaskFlow f;

		public FinishFlowEvent(long ts, TaskFlow f, ElementInstance fe) {
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

	/**
	 * @return the engine
	 */
	public ElementEngine getEngine() {
		return engine;
	}
	
}
