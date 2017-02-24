/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.TreeMap;
import java.util.Map.Entry;

import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.variable.EnumVariable;
import es.ull.iis.util.Prioritizable;

/**
 * @author Iván Castilla
 *
 */
public class Element extends EventSource implements Prioritizable {
	/** Element type */
	protected ElementType elementType;
	/** First step of the flow of the element */
	protected final InitializerFlow initialFlow;
	private ElementEngine engine = null;
	
	public Element(Model model, ElementType elementType, InitializerFlow initialFlow) {
		super(model, model.getNewElementId(), "E");
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
		model.getInfoHandler().notifyInfo(new ElementInfo(model.getSimulationEngine(), this, elementType, ElementInfo.Type.START, model.getSimulationEngine().getTs()));
		simul.addActiveElement(this);
		if (initialFlow != null) {
			return new RequestFlowEvent(ts, initialFlow, mainThread.getInstanceDescendantWorkThread(initialFlow));
		}
		else
			return onDestroy();
	}

	@Override
	public DiscreteEvent onDestroy() {
		model.getInfoHandler().notifyInfo(new ElementInfo(model.getSimulationEngine(), this, elementType, ElementInfo.Type.FINISH, model.getSimulationEngine().getTs()));
		simul.removeActiveElement(this);
		return new DefaultFinalizeEvent();
	}

	public void addRequestEvent(Flow f, FlowExecutor fe) {
		model.getSimulationEngine().addEvent(new RequestFlowEvent(model.getSimulationEngine().getTs(), f, fe));
	}
	
	public void addFinishEvent(long ts, FlowExecutor fe) {
		model.getSimulationEngine().addEvent(new FinishFlowEvent(ts, fe));
	}
	
	/**
	 * Requests a flow.
	 * @author Iván Castilla Rodríguez
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
			flowHandler.request(fe);
		}
	}
	
	/**
	 * Finishes a flow. 
	 * @author Iván Castilla Rodríguez
	 */
	public class FinishFlowEvent extends DiscreteEvent {
		/** The work thread that executes the finish */
		private final FlowExecutor fe;

		public FinishFlowEvent(long ts, FlowExecutor fe) {
			super(ts);
			this.fe = fe;
		}		

		@Override
		public void event() {
			flowHandler.finish(fe);
		}
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		engine = simul.getElementEngineInstance(this);
	}
	
}
