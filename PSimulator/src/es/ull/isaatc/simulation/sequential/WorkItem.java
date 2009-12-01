/**
 * 
 */
package es.ull.isaatc.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;

import es.ull.isaatc.simulation.sequential.flow.SingleFlow;
import es.ull.isaatc.simulation.common.info.ResourceInfo;

/**
 * Represents an element carrying out an activity. Work items are used as part of a work thread
 * every time a new activity has to be performed.
 * @author Iván Castilla Rodríguez
 */
public class WorkItem implements es.ull.isaatc.simulation.common.WorkItem {
	/** Work Thread this work item belongs to */
	final protected WorkThread wThread;
	final private Element elem;
    /** The current flow of this thread */	
	protected SingleFlow flow;
    /** Activity wrapped with this flow */
    protected Activity act;
    /** The workgroup which is used to carry out this flow. If <code>null</code>, 
     * the flow has not been carried out. */
    protected Activity.ActivityWorkGroup executionWG = null;
    /** List of caught resources */
    protected ArrayDeque<Resource> caughtResources;
	/** The arrival order of this work item relatively to the rest of work items 
	 * in the same activity manager. */
	protected int arrivalOrder;
	/** The simulation timestamp when this work item was requested. */
	protected long arrivalTs = -1;
	/** The time left to finish the activity. Used in interruptible activities. */
	protected long timeLeft = -1;
	
	/**
	 * Creates a new work item belonging to work thread wThread.
	 */
	public WorkItem(WorkThread wThread) {
		this.wThread = wThread;
		this.elem = wThread.getElement();
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
        return elem;
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

	/**
	 * Returns the time required to finish the current single flow (only for interruptible activities) 
	 * @return the time required to finish the current single flow 
	 */
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
	public int compareTo(es.ull.isaatc.simulation.common.WorkItem o) {
		if (wThread.getIdentifier() < o.getIdentifier())
			return -1;
		if (wThread.getIdentifier() > o.getIdentifier())
			return 1;
		return 0;
	}

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

	protected long catchResources(ArrayDeque<Resource> caughtResources) {
		this.caughtResources = caughtResources;
    	long auxTs = Long.MAX_VALUE;
    	for (Resource res : caughtResources) {
    		auxTs = Math.min(auxTs, res.catchResource(this));;
            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
    	}
		return auxTs;
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
				es.ull.isaatc.simulation.sequential.Activity.CancelListEntry entry = act.cancellationList.get(i);
				if (res.currentResourceType == entry.rt) {
					long actualTs = elem.getTs();
					res.setNotCanceled(false);
					flow.simul.getInfoHandler().notifyInfo(new ResourceInfo(flow.simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, actualTs));
					res.generateCancelPeriodOffEvent(actualTs, entry.dur);
				}
			}
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

}
