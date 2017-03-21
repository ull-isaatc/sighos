package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.Identifiable;
import es.ull.iis.simulation.model.WorkToken;
import es.ull.iis.simulation.model.flow.BasicFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.util.Prioritizable;

/**
 * A sequential branch of activities in an element's flow. Represents an element instance, so
 * multiple instances can be active at the same time.<p>
 * There are three types of Work threads which require a different method to be created:
 * <ol>
 * <li>Main thread. Is the element's main thread. Must be created by invoking the static method
 * {@link #getInstanceMainWorkThread(ElementEngine)}</li>
 * <li>Descendant thread</li>A thread created to carry out the inner flows of a structured flow.
 * To invoke, use: {@link #getInstanceDescendantWorkThread()}</li>
 * <li>Subsequent thread</li>A thread created to carry out a new flow after a split.
 * To invoke, use: {@link #getInstanceSubsequentWorkThread(boolean, Flow, WorkToken)}</li>
 * </ol><p>
 *  A work thread has an associated token, which can be true or false. A false token is used
 *  only for synchronization purposes and doesn't execute task flows. 
 * @author Iván Castilla Rodríguez
 */
public class FlowExecutor implements Identifiable, Prioritizable, Comparable<FlowExecutor> {
	/** Thread's Counter. Useful for identifying each single flow */
	private static AtomicInteger counter = new AtomicInteger();
    // Avoiding deadlocks (time-overlapped resources)
    /** List of conflictive elements */
    private ConflictZone conflicts;
    /** Amount of possible conflictive resources in the solution */
    private int conflictiveResources = 0;
    /** Stack of nested semaphores */
	private ArrayList<Semaphore> semStack;
    
    /** 
     * Creates a new work thread. The constructor is private since it must be invoked from the 
     * <code>getInstance...</code> methods.
     * @param token An object containing the state of the thread  
     * @param elem Element owner of this thread
     * @param initialFlow The first flow to be executed by this thread
     * @param parent The parent thread, if this thread is included within a structured flow
     */
    private FlowExecutor(WorkToken token, ElementEngine elem, FlowExecutor parent) {
    }

	/**
	 * Sets the counter used to generate work threads' identifiers. 
	 * @param counter The new counter.
	 */
	public static void setCounter(int counter) {
		FlowExecutor.counter.set(counter);
	}
	
	/**
	 * Returns the current counter used to generate work threads' identifiers.
	 * @return The work threads' current counter
	 */
	public static int getCounter() {
		return counter.get();
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
     */
    protected void releaseCaughtResources() {
        // Generate unavailability periods.
        for (Resource res : caughtResources) {
        	final long cancellation = act.getResourceCancelation(res.currentResourceType);
        	if (cancellation > 0) {
				final long currentTs = elem.getTs();
				res.setNotCanceled(false);
				flow.simul.notifyInfo(null).notifyInfo(new ResourceInfo(flow.simul, res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, currentTs));
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

}
