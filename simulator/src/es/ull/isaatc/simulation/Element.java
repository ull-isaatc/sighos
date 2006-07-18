package es.ull.isaatc.simulation;

import java.util.ArrayList;

import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.sync.Semaphore;
import es.ull.isaatc.util.*;

/**
 * Represents elements that make use of activitiy flows in order to carry out 
 * their events.
 * @author Iván Castilla Rodríguez
 */
public class Element extends BasicElement implements RecoverableState<ElementState> {
    /** Workgroup that is currently being used. A null value indicates that this 
     element is not currently performing any activity. */
    protected WorkGroup currentWG = null;
    /** Activity flow of the element */
    protected Flow flow = null;
    /** Stores the requested presential activities (requested[0]) and non-presential 
     ones (requested[1]) */
    protected ArrayList<SingleFlow> []requested = new ArrayList[2];
    /** Amount of pending presential activities (pending[0]) and non-presential 
     ones (pending[1]) */
    protected int []pending;
    /** List of caught resources */
    protected ArrayList<Resource> caughtResources;
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    protected ConflictZone conflicts;
    /** Stack of nested semaphores */
	protected ArrayList<Semaphore> semStack;
	/** Element type */
	protected ElementType elementType;

    /**
     * Creates a new element.
     * @param id Identificador del elemento
     * @param simul Simulation object
     */
	public Element(int id, Simulation simul, ElementType et) {
        super(id, simul);
        this.elementType = et;
        requested[0] = new ArrayList<SingleFlow>();
        requested[1] = new ArrayList<SingleFlow>();
        caughtResources = new ArrayList<Resource>();
	}

    /**
     * If the element is currently performing an activity, returns the workgroup that the 
     * element is using. If the element is not performing any activity, returns null.
     * @return The current workgroup used by this element.
     */
    protected WorkGroup getCurrentWG() {
        return currentWG;
    }
    
    /**
     * Sets the current workgroup used by this element to carry out an activity.
     * @param currentWG The workgroup that the element is going to use. A null value indicates
     * that the element has finished performing an activity.
     */
    protected void setCurrentWG(WorkGroup currentWG) {
        this.currentWG = currentWG;
    }

    /**
     * Returns the list of the elements currently used by the element. 
	 * @return Returns the list of resources caught by this element.
	 */
	protected ArrayList<Resource> getCaughtResources() {
		return caughtResources;
	}

	/**
	 * Adds a resource to the list of resources caught by this element.
	 * @param res A new resource.
	 */
	protected void addCaughtResource(Resource res) {
		caughtResources.add(res);
	}

    /**
     * Releases the resources caught by an element.
     * @return A list of activity managers affected by the released resources
     */
    protected ArrayList<ActivityManager> releaseResources() {
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        for (Resource res : caughtResources) {
        	print(Output.MessageType.DEBUG, "Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		ArrayList<ActivityManager> auxList = res.getCurrentManagers();
        		for (int j = 0; j < auxList.size(); j++)
        			if (!amList.contains(auxList.get(j)))
        				amList.add(auxList.get(j));
        	}
        }
        caughtResources.clear();
        return amList;
    }
	
	/**
     * Returns the activity flow of this element.
     * @return The activity flow of the element.
     */
    public es.ull.isaatc.simulation.Flow getFlow() {
        return flow;
    }
    
    /**
     * Sets the activity flow of this element.
     * @param flow New value of property flow.
     */
    public void setFlow(es.ull.isaatc.simulation.Flow flow) {
        this.flow = flow;
    }

    /**
     * Searches a single flow in the flow of this element.
     * @param id The single flow's identifier 
     * @return A single flow with the corresponding identifier; null if there isn't a single
     * flow with this identifier.
     */
    public SingleFlow searchSingleFlow(int id) {
    	return flow.search(id);
    }
    
    /**
     * Requests the first activities. The simulation's active-element-list is updated. 
     */
    protected void init() {
    	simul.notifyListeners(new ElementInfo(this, ElementInfo.Type.START, ts, elementType.getIdentifier()));
        simul.addActiveElement(this);
        if (flow != null) {
        	pending = flow.countActivities();
        	flow.request();
        }
        else
        	notifyEnd();
    }

	@Override
	protected void end() {
        simul.removeActiveElement(this);
	}
    
    /**
     * Adds a new activity (single flow) to the requested list.
     * @param f Single flow added to the requested list.
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
     * @param f Single flow removed from the requested list.
     */
    protected synchronized void decRequested(SingleFlow f) {
        if (f.isPresential()) {
            requested[0].remove(f);
            pending[0]--;
        }
        else {
            requested[1].remove(f);
            pending[1]--;
        }
        if ((pending[1] + pending[0]) == 0) 
            notifyEnd();
    }

    /**
     * Requests an activity by means of a "RequestActivityEvent".
     * @param f Single flow requested.
     */
    protected void requestActivity(SingleFlow f) {
        RequestActivityEvent e = new RequestActivityEvent(ts, f);
        addEvent(e);
    }

    /**
     * Updates the element timestamp, catch the corresponding resources and produces 
     * a "FinalizeActivityEvent".
     * @param f Single flow requested.
     */
    protected void carryOutActivity(SingleFlow f) {
    	LogicalProcess lp = f.getActivity().getManager().getLp();
        setTs(lp.getTs());
        currentWG.catchResources(this);
        simul.notifyListeners(new ElementInfo(this, ElementInfo.Type.STAACT, ts, f.getActivity().getIdentifier()));
//    	simul.addStatistic(new ElementStatistics(id, ElementStatistics.STAACT, ts, f.getActivity().getIdentifier()));
        print(Output.MessageType.DEBUG, "Starts\t" + f.getActivity(), 
        		"Starts\t" + f.getActivity() + "\t" + f.getActivity().getDescription());
        FinalizeActivityEvent e = new FinalizeActivityEvent(ts + currentWG.getDuration(), f);
        addEvent(e);
    }
	
    /**
     * Creates a new conflict zone. This method should be invoked previously to
     * any activity request.
     */
	protected void resetConflictZone() {
        conflicts = new ConflictZone(this);
	}
	
	/**
	 * Establish a different conflict zone for this element.
	 * @param zone The new conflict zone for this element.
	 */
	protected void setConflictZone(ConflictZone zone) {
		conflicts = zone;
	}
	
	/**
	 * Removes this element from its conflict list. This method is invoked in case
	 * the element detects that it can not carry out an activity.
	 */
	protected void removeFromConflictZone() {
		conflicts.remove(this);
	}
	
	/**
	 * Returns the conflict zone of this element.
	 * @return The conflict zone of the element.
	 */
	protected ConflictZone getConflictZone() {
		return conflicts;
	}
	
	/**
	 * Merges the conflict list of this element and other one. Since one zonflict zone must
	 * be merged into the other, the election of the element which "recibes" the merging operation
	 * depends on the id of the element: the element with lower id "recibes" the merging, and the
	 * other one "produces" the operation.
	 * @param e The element whose conflict zone must be merged. 
	 */
	protected void mergeConflictList(Element e) {
		// If it's the same list there's no need of merge
		if (conflicts != e.getConflictZone()) {
			int result = this.compareTo(e); 
			if (result < 0)
				conflicts.merge(e.getConflictZone());
			else if (result > 0)
				e.getConflictZone().merge(conflicts);
		}
	}
	
	
	/**
	 * Obtains the stack of semaphores from the conflict zone and goes through
	 * this stack performing a wait operation on each semaphore.
	 */
	protected void waitConflictSemaphore() {
		semStack = conflicts.getSemaphores();
		for (Semaphore sem : semStack)
			sem.waitSemaphore();
	}
	
	/** 
	 * Goes through the stack of semaphores performing a signal operation on each semaphore.
	 */
	protected void signalConflictSemaphore() {
		for (Semaphore sem : semStack)
			sem.signalSemaphore();
	}
    
	public String getObjectTypeIdentifier() {
		return "E";
	}

	/**
	 * @return the elementType
	 */
	public ElementType getElementType() {
		return elementType;
	}

	public ElementState getState() {
		ElementState state = null;
		int requestedFlows[][] = new int[2][];
		for (int i = 0; i < requested.length; i++) {
			requestedFlows[i] = new int[requested[i].size()];
			for (int j = 0; j < requested[i].size(); j++)
				requestedFlows[i][j] = requested[i].get(j).getIdentifier();
		}
		if (currentWG == null)
			state = new ElementState(id, elementType.getIdentifier(), flow.getState(), pending, requestedFlows);
		else {
			state = new ElementState(id, elementType.getIdentifier(), flow.getState(), currentWG.getActivity().getIdentifier(), currentWG.getIdentifier(), pending, requestedFlows);
			for(Resource r : caughtResources)
				state.add(r.getIdentifier());
		}
		return state;
	}

	public void setState(ElementState state) {
		if (state.getCurrentActId() != -1) {
			Activity act = simul.getActivity(new Integer(state.getCurrentActId()));
			currentWG = act.getWorkGroup(state.getCurrentWGId());
			for (Integer rId : state.getCaughtResources())
				caughtResources.add(simul.getResourceList().get(rId));
		}		
		if (state.getFlowState() instanceof SequenceFlowState)
			flow = new SequenceFlow(this);
		else if (state.getFlowState() instanceof SimultaneousFlowState)
			flow = new SimultaneousFlow(this);
		else if (state.getFlowState() instanceof SingleFlowState)
			flow = new SingleFlow(this, simul.getActivity(((SingleFlowState)state.getFlowState()).getActId()));
		flow.setState(state.getFlowState());
		pending = state.getPending();
		for (int i = 0; i < state.getRequested().length; i++)
			for (int j = 0; j < state.getRequested()[i].length; j++)
				requested[i].add(flow.search(state.getRequested()[i][j]));		
	}
	
    /**
     * Requests an activity.
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
        	simul.notifyListeners(new ElementInfo(Element.this, ElementInfo.Type.REQACT, ts, flow.getActivity().getIdentifier()));
            print(Output.MessageType.DEBUG, "Requests\t" + flow.getActivity(), 
            		"Requests\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());
            flow.getActivity().getManager().requestActivity(flow);
        }
    }

    /**
     * Informs of the availability of the element.
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
            flow.getActivity().getManager().availableElement(flow);
        }        
    }
    
    /**
     * Finish an activity.
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
        	simul.notifyListeners(new ElementInfo(Element.this, ElementInfo.Type.ENDACT, ts, flow.getActivity().getIdentifier()));
            print(Output.MessageType.DEBUG, "Finishes\t" + flow.getActivity(), 
            		"Finishes\t" + flow.getActivity() + "\t" + flow.getActivity().getDescription());
            flow.getActivity().getManager().finalizeActivity(Element.this);
            flow.finish();
            // Checks if there are pending activities that haven't noticed the element availability
            // MOD 9/01/06 REVISAR
            for (int i = 0; (currentWG == null) && (i < requested[0].size()); i++) {
                AvailableElementEvent e = new AvailableElementEvent(ts, requested[0].get(i));
                addEvent(e);
            }
        }

		/**
		 * @return Returns the flow.
		 */
		public SingleFlow getFlow() {
			return flow;
		}
    }

}
