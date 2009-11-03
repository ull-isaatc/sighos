/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.util.Prioritizable;

/**
 * Represents an element carrying out an activity. Work items are used as part of a work thread
 * every time a new activity has to be performed.
 * @author Iv�n Castilla Rodr�guez
 */
public class WorkItem implements Comparable<WorkItem>, Prioritizable {
	/** Work Thread this work item belongs to */
	final protected WorkThread wThread;
    /** The current flow of this thread */	
	protected SingleFlow flow;
    /** Activity wrapped with this flow */
    protected Activity act;
    /** The workgroup which is used to carry out this flow. If <code>null</code>, 
     * the flow has not been carried out. */
    protected Activity.ActivityWorkGroup executionWG = null;
    /** List of caught resources */
    protected ArrayList<Resource> caughtResources;
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    protected ConflictZone conflicts;
    /** Stack of nested semaphores */
	protected ArrayList<Semaphore> semStack;
	/** The arrival order of this work item relatively to the rest of work items 
	 * in the same activity manager. */
	protected int arrivalOrder;
	/** The simulation timestamp when this work item was requested. */
	protected double arrivalTs = Double.NaN;
	/** The time left to finish the activity. Used in interruptible activities. */
	protected double timeLeft = Double.NaN;
	
	/**
	 * Creates a new work item belonging to work thread wThread.
	 */
	public WorkItem(WorkThread wThread) {
		this.wThread = wThread;
		caughtResources = new ArrayList<Resource>();
	}

	/**
	 * Prepares this work item to be used again for a new activity.
	 * @param flow The current single flow of the thread. 
	 */
	public void reset(SingleFlow flow) {
		this.flow = flow;
		this.act = flow.getActivity();
		executionWG = null;
		arrivalTs = Double.NaN;
		timeLeft = Double.NaN;
	}
	
    /**
     * Returns the work thread this work item belongs to.
	 * @return the work thread this work item belongs to
	 */
	public WorkThread getWorkThread() {
		return wThread;
	}

	/**
     * Returns the activity wrapped with the current single flow.
     * @return Activity wrapped with the current single flow.
     */
    public Activity getActivity() {
        return act;
    }   
	
    /**
     * Gets the current single flow
	 * @return the current single flow
	 */
	public SingleFlow getFlow() {
		return flow;
	}

	/**
     * Returns the element which carries out this flow.
     * @return The associated element.
     */
    public Element getElement() {
        return wThread.getElement();
    }
    
	/**
	 * Returns the order this item occupies among the rest of work items.
	 * @return the order of arrival of this work item to request the activity
	 */
	public int getArrivalOrder() {
		return arrivalOrder;
	}

	/**
	 * Sets the order this item occupies among the rest of work items.
	 * @param arrivalOrder the order of arrival of this work item to request the activity
	 */
	public void setArrivalOrder(int arrivalOrder) {
		this.arrivalOrder = arrivalOrder;
	}

	/**
	 * Returns the timestamp when this work item arrives to request the current single flow.
	 * @return the timestamp when this work item arrives to request the current single flow
	 */
	public double getArrivalTs() {
		return arrivalTs;
	}

	/**
	 * Sets the timestamp when this work item arrives to request the current single flow.
	 * @param arrivalTs the timestamp when this work item arrives to request the current single flow
	 */
	public void setArrivalTs(double arrivalTs) {
		this.arrivalTs = arrivalTs;
	}

	/**
	 * Returns the time required to finish the current single flow (only for interruptible activities) 
	 * @return the time required to finish the current single flow 
	 */
	public double getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Sets the time required to finish the current single flow .
	 * @param timeLeft the time required to finish the current single flow 
	 */
	public void setTimeLeft(double timeLeft) {
		this.timeLeft = timeLeft;
	}

    /**
     * Returns the priority of the element owner of this flow
     * @return The priority of the associated element.
     */
    public int getPriority() {
    	return wThread.getPriority();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
	public int compareTo(WorkItem o) {
		if (wThread.getIdentifier() < o.wThread.getIdentifier())
			return -1;
		if (wThread.getIdentifier() > o.wThread.getIdentifier())
			return 1;
		return 0;
	}    
	
	public boolean equals(Object o) {
		if (((WorkItem)o).wThread.getIdentifier() == wThread.getIdentifier())
			return true;
		return false;
	}
	
    /**
     * Returns the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 */
	public Activity.ActivityWorkGroup getExecutionWG() {
		return executionWG;
	}

	/**
	 * When the single flow can be carried out, sets the workgroup used to
	 * carry out the activity.
	 * @param executionWG the workgroup which is used to carry out this flow.
	 */
	public void setExecutionWG(Activity.ActivityWorkGroup executionWG) {
		this.executionWG = executionWG;
	}

	/**
     * Returns the list of the resources currently used by the element. 
	 * @return Returns the list of resources caught by this element.
	 */
	public ArrayList<Resource> getCaughtResources() {
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
     * Releases the resources caught by this item to perform the activity.
     * @return A list of activity managers affected by the released resources
     */
    protected ArrayList<ActivityManager> releaseCaughtResources() {
        ArrayList<ActivityManager> amList = new ArrayList<ActivityManager>();
        // Generate unavailability periods.
        for (Resource res : caughtResources) {
			for (int i = 0; i < act.cancellationList.size(); i++) {
				es.ull.isaatc.simulation.Activity.CancelListEntry entry = act.cancellationList.get(i);
				if (res.currentResourceType == entry.rt) {
					double actualTs = getElement().ts;
					res.setNotCanceled(false);
					flow.simul.getInfoHandler().notifyInfo(new ResourceInfo(flow.simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, actualTs));
					res.generateCancelPeriodOffEvent(actualTs, entry.dur);
				}
			}
			getElement().debug("Returned " + res);
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
	 * Establish a different conflict zone for this work item.
	 * @param zone The new conflict zone for this work item.
	 */
	protected void setConflictZone(ConflictZone zone) {
		conflicts = zone;
	}
	
	/**
	 * Removes this single flow from its conflict list. This method is invoked in case
	 * the work item detects that it can not carry out an activity.
	 */
	protected void removeFromConflictZone() {
		conflicts.remove(this);
	}
	
	/**
	 * Returns the conflict zone of this work item.
	 * @return The conflict zone of this work item.
	 */
	protected ConflictZone getConflictZone() {
		return conflicts;
	}
	
	/**
	 * Merges the conflict list of this work item and other one. Since one conflict zone must
	 * be merged into the other, the election of the work item which "recibes" the merging 
	 * operation depends on the id of the work item: the item with lower id "recibes" 
	 * the merging, and the other one "produces" the operation.
	 * @param wi The work item whose conflict zone must be merged. 
	 */
	protected void mergeConflictList(WorkItem wi) {
		int result = conflicts.compareTo(wi.getConflictZone());
		if (result != 0) {
			getElement().debug("MUTEX\tmerging\t" + wi.getElement());
			if (result < 0)
				conflicts.safeMerge(getElement(), wi.getConflictZone());
			else if (result > 0) 
				wi.getConflictZone().safeMerge(getElement(), conflicts);
		}
	}
	
	/**
	 * Obtains the stack of semaphores from the conflict zone and goes through
	 * this stack performing a wait operation on each semaphore.
	 */
	protected void waitConflictSemaphore() {
		getElement().debug("MUTEX\trequesting\tConflicts\t" + conflicts);
		semStack = conflicts.getSemaphores(this);
		try {
			for (Semaphore sem : semStack)
				sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getElement().debug("MUTEX\tadquired\tConflicts\t" + conflicts);
	}
	
	/** 
	 * Goes through the stack of semaphores performing a signal operation on each semaphore.
	 */
	protected void signalConflictSemaphore() {
		getElement().debug("MUTEX\treleasing\tConflicts");
		for (Semaphore sem : semStack)
			sem.release();
		getElement().debug("MUTEX\tfreed\tConflicts");		
	}

}