package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.util.*;

/**
 * Represents elements that make use of activitiy flows in order to carry out
 * their events.
 * 
 * @author Iván Castilla Rodríguez
 */
public class Element extends BasicElement implements RecoverableState<ElementState> {
	/** Element type */
	protected ElementType elementType;

	/** Activity flow of the element */
	protected Flow flow = null;

	/**
	 * Stores the requested presential activities (requested[0]) and
	 * non-presential ones (requested[1])
	 */
	protected ArrayList<SingleFlow>[] requested = new ArrayList[2];

	/**
	 * Amount of pending presential activities (pending[0]) and non-presential
	 * ones (pending[1])
	 */
	protected int[] pending;

	/** Presential single flow which the element is currently carrying out */
	protected SingleFlow currentSF = null;

	/**
	 * Creates a new element.
	 * 
	 * @param id
	 *            Identificador del elemento
	 * @param simul
	 *            Simulation object
	 */
	public Element(int id, Simulation simul, ElementType et) {
		super(id, simul);
		this.elementType = et;
		requested[0] = new ArrayList<SingleFlow>();
		requested[1] = new ArrayList<SingleFlow>();
	}

	/**
	 * If the element is currently performing an activity, returns the single
	 * flow that the element is using. If the element is not performing any
	 * activity, returns null.
	 * 
	 * @return The current single flow used by this element.
	 */
	protected SingleFlow getCurrentSF() {
		return currentSF;
	}

	/**
	 * Sets the current single flow whose activity is carrying out this element.
	 * 
	 * @param currentSF
	 *            The single flow that the element is going to use. A null value
	 *            indicates that the element has finished performing an
	 *            activity.
	 */
	protected void setCurrentSF(SingleFlow currentSF) {
		this.currentSF = currentSF;
	}

	/**
	 * Returns the activity flow of this element.
	 * 
	 * @return The activity flow of the element.
	 */
	public es.ull.isaatc.simulation.Flow getFlow() {
		return flow;
	}

	/**
	 * Sets the activity flow of this element.
	 * 
	 * @param flow
	 *            New value of property flow.
	 */
	public void setFlow(es.ull.isaatc.simulation.Flow flow) {
		this.flow = flow;
	}

	/**
	 * Returns the element's priority.
	 * 
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return elementType.getPriority();
	}

	/**
	 * Searches a single flow in the flow of this element.
	 * 
	 * @param id
	 *            The single flow's identifier
	 * @return A single flow with the corresponding identifier; null if there
	 *         isn't a single flow with this identifier.
	 */
	public SingleFlow searchSingleFlow(int id) {
		return flow.search(id);
	}

	/**
	 * Requests the first activities. The simulation's active-element-list is
	 * updated.
	 */
	protected void init() {
		simul.notifyListeners(new ElementInfo(this, ElementInfo.Type.START, ts,
				elementType.getIdentifier()));
		simul.addActiveElement(this);
		if (flow != null) {
			pending = flow.countActivities();
			ArrayList<SingleFlow> sfList = flow.request();
			for (SingleFlow sf : sfList)
				addEvent(new RequestActivityEvent(ts, sf));
		} else
			notifyEnd();
	}

	@Override
	protected void end() {
		simul.notifyListeners(new ElementInfo(this, ElementInfo.Type.FINISH,
				ts, pending[0] + pending[1]));
		simul.removeActiveElement(this);
	}

	/**
	 * Adds a new activity (single flow) to the requested list.
	 * 
	 * @param f
	 *            Single flow added to the requested list.
	 */
	protected synchronized void incRequested(SingleFlow f) {
		if (f.isPresential())
			requested[0].add(f);
		else
			requested[1].add(f);
	}

	/**
	 * Removes an activity (single flow) from the requested (and the pending)
	 * list. If there are no more pending activities, the element produces a
	 * finalize event and finish its execution.
	 * 
	 * @param f
	 *            Single flow removed from the requested list.
	 */
	protected synchronized void decRequested(SingleFlow f) {
		if (f.isPresential()) {
			requested[0].remove(f);
			pending[0]--;
		} else {
			requested[1].remove(f);
			pending[1]--;
		}
		if ((pending[1] + pending[0]) == 0)
			notifyEnd();
	}

	/**
	 * Updates the element timestamp, catch the corresponding resources and
	 * produces a "FinalizeActivityEvent".
	 * 
	 * @param sf
	 *            Single flow requested.
	 */
	protected void carryOutActivity(SingleFlow sf) {
		LogicalProcess lp = sf.getActivity().getManager().getLp();
		setTs(lp.getTs());
		sf.getExecutionWG().catchResources(sf);
		simul.notifyListeners(new ElementInfo(this, ElementInfo.Type.STAACT,
				ts, sf.getActivity().getIdentifier()));
		debug("Starts\t" + sf.getActivity() + "\t" + sf.getActivity().getDescription());
		FinalizeActivityEvent e = new FinalizeActivityEvent(ts
				+ sf.getExecutionWG().getDuration(), sf);
		addEvent(e);
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "E";
	}

	/**
	 * Returns the element type this element belongs to.
	 * 
	 * @return the elementType
	 */
	public ElementType getElementType() {
		return elementType;
	}

	/**
	 * Returns the state of this element. The state of an element consists on
	 * the state of its flow, the requested and pending flows, and the current
	 * single flow (if being used).
	 * 
	 * @return The state of this element.
	 */
	public ElementState getState() {
		ElementState state = null;
		int requestedFlows[][] = new int[2][];
		for (int i = 0; i < requested.length; i++) {
			requestedFlows[i] = new int[requested[i].size()];
			for (int j = 0; j < requested[i].size(); j++)
				requestedFlows[i][j] = requested[i].get(j).getIdentifier();
		}
		if (currentSF == null)
			state = new ElementState(id, elementType.getIdentifier(), flow
					.getState(), pending, requestedFlows);
		else
			state = new ElementState(id, elementType.getIdentifier(), flow
					.getState(), pending, requestedFlows, currentSF
					.getIdentifier());
		return state;
	}

	/**
	 * Sets the state of this element. The state of an element consists on the
	 * state of its flow, the requested and pending flows, and the current
	 * single flow (if being used).
	 * 
	 * @param state
	 *            The new state of this element.
	 */
	public void setState(ElementState state) {
		if (state.getFlowState() instanceof SequenceFlowState)
			flow = new SequenceFlow(this);
		else if (state.getFlowState() instanceof SimultaneousFlowState)
			flow = new SimultaneousFlow(this);
		else if (state.getFlowState() instanceof SingleFlowState)
			flow = new SingleFlow(this, simul
					.getActivity(((SingleFlowState) state.getFlowState())
							.getActId()));
		flow.setState(state.getFlowState());
		if (state.getCurrentSFId() != -1)
			currentSF = searchSingleFlow(state.getCurrentSFId());
		pending = state.getPending();
		for (int i = 0; i < state.getRequested().length; i++)
			for (int j = 0; j < state.getRequested()[i].length; j++)
				requested[i].add(flow.search(state.getRequested()[i][j]));
	}

	/**
	 * Requests an activity. Checks if the activity is feasible and the element
	 * is not performing another activity.
	 * 
	 * @author Iván Castilla Rodríguez
	 */
	public class RequestActivityEvent extends BasicElement.DiscreteEvent {
		/** The flow requested */
		SingleFlow flow;

		public RequestActivityEvent(double ts, SingleFlow flow) {
			super(ts, flow.getActivity().getManager().getLp());
			this.flow = flow;
		}

		public void event() {
			simul.notifyListeners(new ElementInfo(Element.this,
					ElementInfo.Type.REQACT, ts, flow.getActivity()
							.getIdentifier()));
			if (isDebugEnabled())
				debug("Requests\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());
			// Beginning MUTEX access to activity manager
			flow.getActivity().getManager().waitSemaphore();
			Activity act = flow.getActivity();
    		debug("MUTEX\trequesting\t" + act + " (req. act.)");    	
            waitSemaphore();
    		debug("MUTEX\tadquired\t" + act + " (req. act.)");    	
			// If the element is not performing a presential activity yet or the
			// activity to be requested is non presential
			if ((currentSF == null) || (!act.isPresential())) {
				if (act.isFeasible(flow)) { // There are enough resources to perform the
									// activity
            		debug("MUTEX\treleasing\t" + act + " (req. act.)");    	
                	signalSemaphore();
            		debug("MUTEX\tfreed\t" + act + " (req. act.)");    	
					carryOutActivity(flow);
				} else {
					act.queueAdd(flow); // The element is introduced in the
										// activity waiting queue
            		debug("MUTEX\treleasing\t" + act + " (req. act.)");    	
                	signalSemaphore();
            		debug("MUTEX\tfreed\t" + act + " (req. act.)");    	
				}
			} else {
				act.queueAdd(flow); // The element is introduced in the activity
									// waiting queue
        		debug("MUTEX\treleasing\t" + act + " (req. act.)");    	
            	signalSemaphore();
        		debug("MUTEX\tfreed\t" + act + " (req. act.)");    	
			}
			// Ending MUTEX access to activity manager
			flow.getActivity().getManager().signalSemaphore();
			incRequested(flow);
		}
	}

	/**
	 * Informs an activity about an available element in its queue.
	 * 
	 * @author Iván Castilla Rodríguez
	 */
	public class AvailableElementEvent extends BasicElement.DiscreteEvent {
		/** Flow informed of the availability of the element */
		SingleFlow flow;

		public AvailableElementEvent(double ts, SingleFlow flow) {
			super(ts, flow.getActivity().getManager().getLp());
			this.flow = flow;
		}

		public void event() {
			// Beginning MUTEX access to activity manager
			flow.getActivity().getManager().waitSemaphore();

			Activity act = flow.getActivity();
    		debug("MUTEX\trequesting\t" + act + " (av. el.)");    	
            waitSemaphore();
    		debug("MUTEX\tadquired\t" + act + " (av. el.)");    	
			debug("Calling availableElement()\t" + act + "\t" + act.getDescription());
			// If the element is not performing a presential activity yet
			if (currentSF == null) {
				if (act.isFeasible(flow)) {
					debug("Can carry out\t" + act + "\t" + flow.getExecutionWG().getIdentifier());
					if (!act.queueRemove(flow))
						error("Unexpected error. Element MUST BE in the activity queue\t" + act);
            		debug("MUTEX\treleasing\t" + act + " (av. el.)");    	
                	signalSemaphore();
            		debug("MUTEX\tfreed\t" + act + " (av. el.)");    	
					carryOutActivity(flow);
				} else {
            		debug("MUTEX\treleasing\t" + act + " (av. el.)");    	
                	signalSemaphore();
            		debug("MUTEX\tfreed\t" + act + " (av. el.)");    	
				}
			} else {
        		debug("MUTEX\treleasing\t" + act + " (av. el.)");    	
            	signalSemaphore();
        		debug("MUTEX\tfreed\t" + act + " (av. el.)");    	
			}
			// Ending MUTEX access to activity manager
			flow.getActivity().getManager().signalSemaphore();
		}
	}

	/**
	 * Finish an activity.
	 * 
	 * @author Iván Castilla Rodríguez
	 */
	public class FinalizeActivityEvent extends BasicElement.DiscreteEvent {
		/** The flow finished */
		SingleFlow flow;

		public FinalizeActivityEvent(double ts, SingleFlow flow) {
			super(ts, flow.getActivity().getManager().getLp());
			this.flow = flow;
		}

		public void event() {
			simul.notifyListeners(new ElementInfo(Element.this,
					ElementInfo.Type.ENDACT, ts, flow.getActivity()
							.getIdentifier()));
			if (isDebugEnabled())
				debug("Finishes\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());

			// Beginning MUTEX access to activity manager
			flow.getActivity().getManager().waitSemaphore();

			ArrayList<ActivityManager> amList = flow.releaseCaughtResources();
			if (flow.isPresential())
				currentSF = null;

			// Ending MUTEX access to activity manager
			flow.getActivity().getManager().signalSemaphore();

			int[] order = RandomPermutation.nextPermutation(amList.size());
			for (int ind : order) {
				ActivityManager am = amList.get(ind);
				am.waitSemaphore();
				am.availableResource();
				am.signalSemaphore();
			}

			ArrayList<SingleFlow> sfList = flow.finish();
			for (SingleFlow sf : sfList)
				addEvent(new RequestActivityEvent(ts, sf));
			decRequested(flow);
			// Checks if there are pending activities that haven't noticed the
			// element availability
			for (int i = 0; (currentSF == null) && (i < requested[0].size()); i++)
				addEvent(new AvailableElementEvent(ts, requested[0].get(i)));
		}

		/**
		 * @return Returns the flow.
		 */
		public SingleFlow getFlow() {
			return flow;
		}
	}

}
