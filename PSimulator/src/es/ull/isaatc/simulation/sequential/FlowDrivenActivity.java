package es.ull.isaatc.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;

import es.ull.isaatc.simulation.sequential.condition.Condition;
import es.ull.isaatc.simulation.sequential.flow.BasicFlow;
import es.ull.isaatc.simulation.sequential.flow.FinalizerFlow;
import es.ull.isaatc.simulation.sequential.flow.Flow;
import es.ull.isaatc.simulation.sequential.flow.InitializerFlow;
import es.ull.isaatc.simulation.sequential.flow.StructuredFlow;
import es.ull.isaatc.simulation.sequential.info.ElementActionInfo;
import es.ull.isaatc.util.RandomPermutation;

/**
 * A task which could be carried out by an element and whose duration depends on the finalization
 * of an internal flow. An activity is characterized by its priority, presentiality, and a set of 
 * workgroups. Each workgroup represents a combination of resource types required for carrying out 
 * the activity, and the duration of the activity when performed with this workgroup.<p>
 * A normal activity is presential, that is, an element carrying out this activity can't make 
 * anything else; and ininterruptible, i.e., once started, the activity keeps its resources until
 * it's finished, even if the resources become unavailable while the activity is being performed.
 * This two characteristics are customizable by means of the <code>Modifier</code> enum type. An 
 * activity can be <code>NONPRESENTIAL</code>, when the element can perform other activities while
 * it's performing this one; and <code>INTERRUPTIBLE</code>, when the activity can be interrupted, 
 * and later continued, if the resources become unavailable while the activity is being performed.
 * @author Iván Castilla Rodríguez 
 */
public class FlowDrivenActivity extends Activity {
	/** 
	 * An artificially created final node. This flow informs the flow-driven
	 * activity that it has being finalized.
	 */
	private BasicFlow virtualFinalFlow = new BasicFlow(simul) {

		public void addPredecessor(Flow newFlow) {
		}

		public void request(WorkThread wThread) {
			Element elem = wThread.getElement();
			elem.addEvent(elem.new FinishFlowEvent(elem.getTs(), wThread.getParent().getWorkItem().getFlow(), wThread.getParent()));
		}

		public void link(Flow successor) {
		}

		public void setRecursiveStructureLink(StructuredFlow parent) {
		}
		
	};
	
	/**
     * Creates a new flow-driven activity.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public FlowDrivenActivity(int id, Simulation simul, String description) {
        this(id, simul, description, 0);
    }

    /**
     * Creates a new flow-driven activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public FlowDrivenActivity(int id, Simulation simul, String description, int priority) {
        super(id, simul, description, priority);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.model.WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, initialFlow, finalFlow, priority, wg));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, initialFlow, finalFlow, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority.
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, es.ull.isaatc.simulation.model.WorkGroup wg) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg, cond);
    }

    /**
     * Creates a new workgroup for this activity. 
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, initialFlow, finalFlow, priority));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param priority Priority of the workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, Condition cond) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, initialFlow, finalFlow, priority, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow) {    	
        return addWorkGroup(initialFlow, finalFlow, 0);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority.
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, Condition cond) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, cond);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        return (ActivityWorkGroup)super.getWorkGroup(wgId);
    }

    /**
     * Catches the resources required and launches the initial flow.
	 * @param wItem Work item requesting this activity 
     */
	@Override
	public void carryOut(WorkItem wItem, ArrayDeque<Resource> solution) {
		Element elem = wItem.getElement();
		wItem.catchResources(solution);
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
		elem.debug("Starts\t" + this + "\t" + description);
		InitializerFlow initialFlow = ((FlowDrivenActivity.ActivityWorkGroup)wItem.getExecutionWG()).getInitialFlow();
		wItem.getWorkThread().getElement().addRequestEvent(initialFlow, wItem.getWorkThread().getInstanceDescendantWorkThread(initialFlow));
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#finish(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public boolean finish(WorkItem wItem) {
		Element elem = wItem.getElement();

		ArrayList<ActivityManager> amList = wItem.releaseCaughtResources();


		int[] order = RandomPermutation.nextPermutation(amList.size());
		for (int ind : order) {
			ActivityManager am = amList.get(ind);
			am.availableResource();
		}

		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Finishes\t" + this + "\t" + description);
        return true;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#request(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public void request(WorkItem wItem) {
		Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);
		// If there are enough resources to perform the activity
		ArrayDeque<Resource> solution = isFeasible(wItem); 
		if (solution != null) {
			carryOut(wItem, solution);
		}
		else {
			queueAdd(wItem); // The element is introduced in the queue
		}
		
	}

	/**
	 * All elements are valid to perform a flow-driven activity.
	 * @param wItem Work item requesting this activity 
	 */
	@Override
	public boolean validElement(WorkItem wItem) {
		return true;
	}
    
	@Override
	public String getObjectTypeIdentifier() {
		return "FACT";
	}
	
	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, a subflow, and the priority of the 
	 * workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends Activity.ActivityWorkGroup {
		/** The first step of the subflow */
	    final protected InitializerFlow initialFlow;
	    /** The last step of the subflow */
	    final protected FinalizerFlow finalFlow;
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param initialFlow The first step of the flow 
	     * @param finalFlow The last step of the flow
	     * @param priority Priority of the workgroup.
	     */    
	    protected ActivityWorkGroup(int id, InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority) {
	        super(id, priority);
	        this.initialFlow = initialFlow;
	        this.finalFlow = finalFlow;
	        finalFlow.link(virtualFinalFlow);
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param initialFlow The first step of the flow 
	     * @param finalFlow The last step of the flow
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, Condition cond) {
	        super(id, priority, cond);
	        this.initialFlow = initialFlow;
	        this.finalFlow = finalFlow;
	        finalFlow.link(virtualFinalFlow);
	    }

	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param initialFlow The first step of the flow 
	     * @param finalFlow The last step of the flow
	     * @param priority Priority of the workgroup.
	     * @param wg Original workgroup
	     */    
	    protected ActivityWorkGroup(int id, InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.model.WorkGroup wg) {
	        super(id, priority, wg);
	        this.initialFlow = initialFlow;
	        this.finalFlow = finalFlow;
	        finalFlow.link(virtualFinalFlow);
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, InitializerFlow initialFlow, FinalizerFlow finalFlow, int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
	        super(id, priority, wg, cond);
	        this.initialFlow = initialFlow;
	        this.finalFlow = finalFlow;
	        finalFlow.link(virtualFinalFlow);
	    }


	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    protected FlowDrivenActivity getActivity() {
	        return FlowDrivenActivity.this;
	    }

	    /**
	     * Returns the first step of the subflow
		 * @return the initialFlow
		 */
		public InitializerFlow getInitialFlow() {
			return initialFlow;
		}

		/**
	     * Returns the last step of the subflow
		 * @return the finalFlow
		 */
		public FinalizerFlow getFinalFlow() {
			return finalFlow;
		}

		@Override
	    public String toString() {
	    	return new String(super.toString());
	    }

	}


}
