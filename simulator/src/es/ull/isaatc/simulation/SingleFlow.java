/*
 * FlujoSimple.java
 *
 * Created on 17 de junio de 2005, 12:47
 */

package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;
import es.ull.isaatc.util.Prioritizable;

/**
 * A single-activity flow. This flow represents a leaf node in the flow tree.<p>
 * A single flow handles the execution of its activity.
 * @author Iván Castilla Rodríguez
 */
public class SingleFlow extends Flow implements Comparable<SingleFlow>, Prioritizable {
	/** Single flows' Counter. Useful for identying each single flow */
	private static int counter = 0;
	/** Single flow's identifier */
	protected int id;
    /** Activity wrapped with this flow */
    protected Activity act;
    /** Indicates if the activity has been already executed */
    protected boolean finished = false;
    /** The workgroup which is used to carry out this flow. If <code>null</code>, 
     * the flow has not been carried out. */
    protected Activity.WorkGroup executionWG = null;
    /** List of caught resources */
    protected ArrayList<Resource> caughtResources;
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    protected ConflictZone conflicts;
    /** Stack of nested semaphores */
	protected ArrayList<Semaphore> semStack;
	/** The arrival order of this single flow relatively to the rest of single flows 
	 * in the same activity manager. */
	protected int arrivalOrder;
	/** The simulation timestamp when this single flow was requested. */
	protected double arrivalTs = Double.NaN;
	/** The time left to finish the activity */
	protected double timeLeft = Double.NaN;
    
    /** 
     * Creates a new parent single flow which wraps an activity. 
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(Element elem, Activity act) {
        super(elem);
        this.act = act;
        id = counter++;
        caughtResources = new ArrayList<Resource>();
    }
    
    /** 
     * Creates a new single flow which wraps an activity. 
     * @param parent This flow's parent.
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
     */
    public SingleFlow(GroupFlow parent, Element elem, Activity act) {
        super(parent, elem);
        if (parent != null)
        	parent.add(this);
        this.act = act;
        id = counter++;
        caughtResources = new ArrayList<Resource>();
    }

    /**
     * Returns the activity wrapped with this flow.
     * @return Activity wrapped with this flow.
     */
    public Activity getActivity() {
        return act;
    }
    
    /**
     * This flow is marked as "finished". 
     * The parent flow is finished too.
     * @return An empty list of single flows.
     */
    protected ArrayList<SingleFlow> finish() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
        finished = true;
        if (parent != null)
            sfList.addAll(parent.finish());
        return sfList;
    }
    
    /**
     * Returns a list that contains this single flow.
     * @return A list with this single flow. 
     */
    protected ArrayList<SingleFlow> request() {
    	ArrayList<SingleFlow> sfList = new ArrayList<SingleFlow>();
    	sfList.add(this);
    	return sfList;
    }
    
    /**
     * A single flow is presential if the activity it wraps is presential. 
     * @return True if the wrapped activity is presential; False in other case.
     */
    protected boolean isPresential() {
        return act.isPresential();
    }
    
    /**
     * Returns an array [1, 0] if the activity wrapped is presential; and an
     * array [0, 1] if the activity is not presential.
     * @return The amount of activities this flow contains.
     */    
    public int[] countActivities() {
        int [] cont = new int[2];
        if (act.isPresential())
            cont[0]++;
        else
            cont[1]++;
        return cont;
    }

	/**
	 * Returns the unique identifier of this single flow.
	 * @return Returns the id.
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * Sets the counter used to generate single flows' identifiers. 
	 * @param counter The new counter.
	 */
	public static void setCounter(int counter) {
		SingleFlow.counter = counter;
	}
	
	/**
	 * Returns the current counter used to generate single flows' identifiers.
	 * @return The single flows' counter
	 */
	public static int getCounter() {
		return counter;
	}

	/**
	 * @return the arrivalOrder
	 */
	public int getArrivalOrder() {
		return arrivalOrder;
	}

	/**
	 * @param arrivalOrder the arrivalOrder to set
	 */
	public void setArrivalOrder(int arrivalOrder) {
		this.arrivalOrder = arrivalOrder;
	}

	/**
	 * @return the arrivalTs
	 */
	public double getArrivalTs() {
		return arrivalTs;
	}

	/**
	 * @param arrivalTs the arrivalTs to set
	 */
	public void setArrivalTs(double arrivalTs) {
		this.arrivalTs = arrivalTs;
	}

	/**
	 * @return the timeLeft
	 */
	public double getTimeLeft() {
		return timeLeft;
	}

	/**
	 * @param timeLeft the timeLeft to set
	 */
	public void setTimeLeft(double timeLeft) {
		this.timeLeft = timeLeft;
	}

	@Override
	protected SingleFlow search(int id) {
		if (this.id == id)
			return this;
		return null;
	}

	@Override
	protected boolean isFinished() {
		return finished;
	}

    /**
     * Returns the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 */
	public Activity.WorkGroup getExecutionWG() {
		return executionWG;
	}

	/**
	 * When the single flow can be carried out, sets the workgroup used to
	 * carry out the activity.
	 * @param executionWG the workgroup which is used to carry out this flow.
	 */
	public void setExecutionWG(Activity.WorkGroup executionWG) {
		this.executionWG = executionWG;
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
     * Releases the resources caught by hissingle flow to perform the activity.
     * @return A list of activity managers affected by the released resources
     */
    protected ArrayList<ActivityManager> releaseCaughtResources() {
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        for (Resource res : caughtResources) {
        	elem.debug("Returned " + res);
        	// The resource is freed
        	if (res.releaseResource()) {
        		// The activity managers involved are included in the list
        		for (ActivityManager am : res.getCurrentManagers())
        			if (!amList.contains(am))
        				amList.add(am);
        	}
        }
        caughtResources.clear();
        return amList;
    }

    /**
     * Creates a new conflict zone. This method should be invoked previously to
     * any activity request.
     */
	protected void resetConflictZone() {
        conflicts = new ConflictZone(this);
	}
	
	/**
	 * Establish a different conflict zone for this single flow.
	 * @param zone The new conflict zone for this single flow.
	 */
	protected void setConflictZone(ConflictZone zone) {
		conflicts = zone;
	}
	
	/**
	 * Removes this single flow from its conflict list. This method is invoked in case
	 * the single flow detects that it can not carry out an activity.
	 */
	protected void removeFromConflictZone() {
		conflicts.remove(this);
	}
	
	/**
	 * Returns the conflict zone of this single flow.
	 * @return The conflict zone of the single flow.
	 */
	protected ConflictZone getConflictZone() {
		return conflicts;
	}
	
	/**
	 * Merges the conflict list of this single flow and other one. Since one zonflict zone must
	 * be merged into the other, the election of the single flow which "recibes" the merging 
	 * operation depends on the id of the single flow: the single flow with lower id "recibes" 
	 * the merging, and the other one "produces" the operation.
	 * @param sf The single flow whose conflict zone must be merged. 
	 */
	protected void mergeConflictList(SingleFlow sf) {
		// If it's the same list there's no need of merge
		elem.debug("MUTEX\tmerging\t" + sf.getElement());
		if (conflicts != sf.getConflictZone()) {
			int result = this.compareTo(sf); 
			if (result < 0)
				conflicts.merge(sf.getConflictZone());
			else if (result > 0)
				sf.getConflictZone().merge(conflicts);
		}
	}
	
	/**
	 * Obtains the stack of semaphores from the conflict zone and goes through
	 * this stack performing a wait operation on each semaphore.
	 */
	protected void waitConflictSemaphore() {
		semStack = conflicts.getSemaphores();
		elem.debug("MUTEX\trequesting\tConflicts");
		try {
			for (Semaphore sem : semStack)
				sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		elem.debug("MUTEX\tadquired\tConflicts");
	}
	
	/** 
	 * Goes through the stack of semaphores performing a signal operation on each semaphore.
	 */
	protected void signalConflictSemaphore() {
		elem.debug("MUTEX\treleasing\tConflicts");
		for (Semaphore sem : semStack)
			sem.release();
		elem.debug("MUTEX\tfreed\tConflicts");		
	}

	/**
	 * Returns the state of this single flow. The state of a single flow consists on the 
	 * activity wrapped, its identifier and the <code>finished</code> atribute. If this 
	 * single flow is currently performing its activity, the workgroup used and the set of
	 * caught resources are stored too.
	 * @return The state of this single flow.
	 */
	public FlowState getState() {
		SingleFlowState state = null;
		if (executionWG != null)
			state = new SingleFlowState(id, act.getIdentifier(), finished, executionWG.getIdentifier(), arrivalTs);
		else
			state = new SingleFlowState(id, act.getIdentifier(), finished, -1, arrivalTs);
		for(Resource r : caughtResources)
			state.add(r.getIdentifier());
		return state;
	}

	/**
	 * Returns the state of this single flow. The state of a single flow consists on the 
	 * activity wrapped, its identifier and the <code>finished</code> atribute. If this 
	 * state shows that this single flow was currently performing its activity, the workgroup 
	 * used and the set of caught resources are restored too.
	 * @param state The new state of this single flow.
	 */
	public void setState(FlowState state) {
		SingleFlowState sfState = (SingleFlowState)state;
		finished = sfState.isFinished();
		id = sfState.getFlowId();
		arrivalTs = sfState.getArrivalTs();
		if (sfState.getExecutionWG() != -1) {
			executionWG = act.getWorkGroup(sfState.getExecutionWG());
			for (Integer rId : sfState.getCaughtResources())
				caughtResources.add(act.getSimul().getResourceList().get(rId));
		}		
	}

    /**
     * Returns the priority of the element owner of this flow
     * @return The priority of the associated element.
     */
    public int getPriority() {
    	return elem.getPriority();
    }

	public int compareTo(SingleFlow o) {
		if (id < o.id)
			return -1;
		if (id > o.id)
			return 1;
		return 0;
	}    
	
	public boolean equals(Object o) {
		if (((SingleFlow)o).id == id)
			return true;
		return false;
	}
	
	public String toString() {
		return "SF" + id + "(" + elem + ")";
	}
}
