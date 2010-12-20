/**
 * 
 */
package es.ull.isaatc.simulation.parallel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.parallel.flow.SingleFlow;

/**
 * Represents an element carrying out an activity. Work items are used as part of a work thread
 * every time a new activity has to be performed.
 * @author Iván Castilla Rodríguez
 */
public class WorkItem implements es.ull.isaatc.simulation.WorkItem {
	/** Work Thread this work item belongs to */
	final private WorkThread wThread;
	/** Element that contains this work item */
	final private Element elem;
    /** The current flow of this thread */	
	private SingleFlow flow;
    /** Activity wrapped with this flow */
    private Activity act;
    /** The workgroup which is used to carry out this flow. If <code>null</code>, 
     * the flow has not been carried out. */
    private Activity.ActivityWorkGroup executionWG = null;
    /** List of caught resources */
    final private ArrayDeque<Resource> caughtResources = new ArrayDeque<Resource>();
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    private ConflictZone conflicts;
    /** Amount of possible conflictive resources in the solution */
    private int conflictiveResources = 0;
    /** Stack of nested semaphores */
	private ArrayList<Semaphore> semStack;
	/** The arrival order of this work item relatively to the rest of work items 
	 * in the same activity manager. */
	private int arrivalOrder;
	/** The simulation timestamp when this work item was requested. */
	private long arrivalTs = -1;
	/** The time left to finish the activity. Used in interruptible activities. */
	private long timeLeft = -1;
	
	/**
	 * Creates a new work item belonging to work thread wThread.
	 * @param wThread WorkThread this item belongs to
	 */
	public WorkItem(WorkThread wThread) {
		this.wThread = wThread;
		elem = wThread.getElement();
	}

	/**
	 * Prepares this work item to be used again for a new activity.
	 * @param flow The current single flow of the thread. 
	 */
	public void reset(SingleFlow flow) {
		this.flow = flow;
		this.act = flow.getActivity();
		executionWG = null;
		arrivalTs = -1;
		timeLeft = -1;
	}
	
    /**
     * Returns the work thread this work item belongs to.
	 * @return the work thread this work item belongs to
	 */
	public WorkThread getWorkThread() {
		return wThread;
	}

	@Override
    public Activity getActivity() {
        return act;
    }   
	
	@Override
	public SingleFlow getFlow() {
		return flow;
	}

	@Override
    public Element getElement() {
        return elem;
    }
    
	@Override
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

	@Override
	public long getArrivalTs() {
		return arrivalTs;
	}

	/**
	 * Sets the timestamp when this work item arrives to request the current single flow.
	 * @param arrivalTs the timestamp when this work item arrives to request the current single flow
	 */
	public void setArrivalTs(long arrivalTs) {
		this.arrivalTs = arrivalTs;
	}

	@Override
	public long getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Sets the time required to finish the current single flow .
	 * @param timeLeft the time required to finish the current single flow 
	 */
	public void setTimeLeft(long timeLeft) {
		this.timeLeft = timeLeft;
	}

    /**
     * Returns the priority of the element owner of this flow
     * @return The priority of the associated element.
     */
    public int getPriority() {
    	return wThread.getPriority();
    }

	@Override
	public int compareTo(es.ull.isaatc.simulation.WorkItem o) {
		final int id = wThread.getIdentifier();
		final int id2 = o.getIdentifier();
		if (id < id2)
			return -1;
		if (id > id2)
			return 1;
		return 0;
	}

	/**
	 * Returns the identifier of the work thread this item belongs to.
	 */
	@Override
	public int getIdentifier() {
		return wThread.getIdentifier();
	}

	
	@Override
	public boolean equals(Object o) {
		if (((WorkItem)o).wThread.getIdentifier() == wThread.getIdentifier())
			return true;
		return false;
	}
	
	@Override
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
	public ArrayDeque<Resource> getCaughtResources() {
		return caughtResources;
	}

	/**
	 * Checks if the list of resources selected to carry out an activity are still valid.
	 * @return True if none of the selected resources is being used by a different element;
	 * false in other case. 
	 */
	public boolean checkCaughtResources() {
		for (Resource res : caughtResources)
			if (!res.checkSolution(this))
				return false;
		return true;
	}
	
	/**
	 * Adds a resource to the list of resources caught by this element.
	 * @param res A new resource.
	 */
	protected void pushResource(Resource res, boolean conflictive) {
		caughtResources.push(res);
		if (conflictive)
			conflictiveResources++;
	}
	
	/**
	 * Removes the last resource caught by this element.
	 * @return The resource removed
	 */
	protected Resource popResource(boolean conflictive) {
		if (conflictive)
			conflictiveResources--;
		return caughtResources.pop();
	}
	
	/**
	 * Returns true if any one of the resources taken to carry out an activity is active in more 
	 * than one AM
	 * @return True if any one of the resources taken to carry out an activity is active in more 
	 * than one AM; false in other case.
	 */
	protected boolean isConflictive() {
		return (conflictiveResources > 0);
	}
	
    /**
     * Catch the resources needed for each resource type to carry out an activity.
     * @return The minimum availability timestamp of the taken resources 
     */
	protected long catchResources() {
    	long auxTs = Long.MAX_VALUE;
    	for (Resource res : caughtResources) {
    		auxTs = Math.min(auxTs, res.catchResource(this));;
            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
    	}
    	// When this point is reached, that means that the resources have been completely taken
    	signalConflictSemaphore();
		return auxTs;
	}
	
    /**
     * Releases the resources caught by this item to perform the activity.
     * @return A list of activity managers affected by the released resources
     */
    protected void releaseCaughtResources() {
        // Generate unavailability periods.
        for (Resource res : caughtResources) {
        	final long cancellation = act.getResourceCancelation(res.currentResourceType);
        	if (cancellation > 0) {
				final long currentTs = elem.getTs();
				res.setNotCanceled(false);
				flow.simul.getInfoHandler().notifyInfo(new ResourceInfo(flow.simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, currentTs));
				res.generateCancelPeriodOffEvent(currentTs, cancellation);
			}
			elem.debug("Returned " + res);
        	// The resource is freed and the AMs are notified
        	if (res.releaseResource())
        		res.notifyCurrentManagers();
        }
        caughtResources.clear();
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
	 * be merged into the other, the election of the work item which "receives" the merging 
	 * operation depends on the id of the work item: the item with lower id "receives" 
	 * the merging, and the other one "produces" the operation.
	 * @param wi The work item whose conflict zone must be merged. 
	 */
	protected void mergeConflictList(WorkItem wi) {
		final int result = conflicts.compareTo(wi.getConflictZone());
		if (result != 0) {
			if (result < 0)
				conflicts.safeMerge(elem, wi.getConflictZone());
			else if (result > 0) 
				wi.getConflictZone().safeMerge(elem, conflicts);
		}
	}
	
	/**
	 * Obtains the stack of semaphores from the conflict zone and goes through
	 * this stack performing a wait operation on each semaphore.
	 */
	protected void waitConflictSemaphore() {
		semStack = conflicts.getSemaphores(this);
		try {
			for (Semaphore sem : semStack)
				sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Goes through the stack of semaphores performing a signal operation on each semaphore.
	 */
	protected void signalConflictSemaphore() {
		for (Semaphore sem : semStack)
			sem.release();
	}

	@Override
	public String toString() {
		return wThread + "\tACT: " + act.getDescription();
	}
}
