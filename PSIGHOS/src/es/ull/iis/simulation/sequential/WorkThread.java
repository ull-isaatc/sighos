package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.util.Prioritizable;

/**
 * A sequential branch of activities in an element's flow. Represents an element instance, so
 * multiple instances can be active at the same time.<p>
 * There are three types of Work threads which require a different method to be created:
 * <ol>
 * <li>Main thread. Is the element's main thread. Must be created by invoking the static method
 * <code>WorkThread.getInstanceMainWorkThread</code></li>
 * <li>Descendant thread</li>A thread created to carry out the inner flows of a structured flow.
 * To invoke, use: getInstanceDescendantWorkThread</li>
 * <li>Subsequent thread</li>A thread created to carry out a new flow after a split.
 * To invoke, use: getInstanceSubsequentWorkThread</li>
 * </ol><p>
 *  A work thread has an associated token, which can be true or false. A false token is used
 *  only for synchronization purposes and doesn't execute task flows. 
 * @author Iv�n Castilla Rodr�guez
 */
public class WorkThread implements es.ull.iis.simulation.model.flow.FlowExecutor, Comparable<WorkThread>, Prioritizable {
	/** Thread's Counter. Useful for identifying each single flow */
	private static int counter = 0;
	/** Thread's internal identifier */
	protected final int id;
    /** Element which carries out this flow. */    
    protected final Element elem; 
    /** The parent element thread */
    protected final WorkThread parent;
    /** The descendant work threads */
	protected final ArrayList<WorkThread> descendants;
    /** Thread's initial flow */
    protected final Flow initialFlow;
	/** A flag to indicate if the thread executes the flow or not */
	protected WorkToken token;
	/** The current flow the thread is in */
	protected Flow currentFlow = null;
	/** The last flow the thread was in */
	protected Flow lastFlow = null;
    /** The workgroup which is used to carry out this flow. If <code>null</code>, 
     * the flow has not been carried out. */
    protected ActivityWorkGroup executionWG = null;
    /** List of caught resources */
    final protected TreeMap<Integer, ArrayDeque<Resource>> caughtResources = new TreeMap<Integer, ArrayDeque<Resource>>();
	/** The arrival order of this work thread relatively to the rest of work threads 
	 * in the same activity manager. */
	protected int arrivalOrder;
	/** The simulation timestamp when this work thread was requested. */
	protected long arrivalTs = -1;
	/** The time left to finish the activity. Used in interruptible activities. */
	protected long timeLeft = -1;
    
    /** 
     * Creates a new work thread. The constructor is private since it must be invoked from the 
     * <code>getInstance...</code> methods.
     * @param token An object containing the state of the thread  
     * @param elem Element owner of this thread
     * @param initialFlow The first flow to be executed by this thread
     * @param parent The parent thread, if this thread is included within a structured flow
     */
    private WorkThread(WorkToken token, Element elem, Flow initialFlow, WorkThread parent) {
    	this.token = token;
        this.elem = elem;
        this.parent = parent;
        descendants = new ArrayList<WorkThread>();
        if (parent != null)
        	parent.addDescendant(this);
        this.initialFlow = initialFlow;
        this.id = counter++;
    }

	/**
	 * Sets the flow currently executed by this workthread 
	 * @param f The flow to be performed
	 */
	public void setCurrentFlow(Flow f) {
    	currentFlow = f;
		executionWG = null;
		arrivalTs = -1;
		timeLeft = -1;    		
	}

    /**
     * Returns the flow being performed.
	 * @return The flow being performed.
	 */
	public Flow getCurrentFlow() {
		return currentFlow;
	}
	    
    /**
     * Notifies the parent this thread has finished.
     */
    public void notifyEnd() {
    	if (parent != null) {
    		parent.removeDescendant(this);
    		if ((parent.descendants.size() == 0) && (parent.currentFlow != null))
    			((TaskFlow)parent.currentFlow).finish(parent);
    	}
    }
    
    /**
     * Adds a thread to the list of descendants.
     * @param wThread Descendant thread
     */
	public void addDescendant(WorkThread wThread) {
		descendants.add(wThread);
	}

	/**
	 * Removes a thread from the list of descendants. If it's the last thread of an element,
	 * the element has to be notified and finished.
	 * @param wThread Descendant thread
	 */
	public void removeDescendant(WorkThread wThread) {
		descendants.remove(wThread);
		if (parent == null && descendants.size() == 0)
			elem.notifyEnd();
	}

	/**
	 * Returns <code>true</code> if this thread is valid.
	 * @return <code>True</code> if this thread is valid; false otherwise.
	 */
	public boolean isExecutable() {
		return token.isExecutable();
	}

	/**
	 * Changes the state of this thread to not valid and restarts the path of visited flows.
	 * @param startPoint The initial flow to control infinite loops with not valid threads. 
	 */
	public void cancel(Flow startPoint) {
		token.reset();
		token.addFlow(startPoint);
	}

	/**
	 * Returns the last flow visited by this workthread
	 * @return the last flow visited by this workthread
	 */
	public Flow getLastFlow() {
		return lastFlow;
	}

	/**
	 * Sets the last flow visited by this thread.
	 * @param lastFlow The lastFlow visited by this thread
	 */
	public void setLastFlow(Flow lastFlow) {
		this.lastFlow = lastFlow;
	}

	/**
     * Returns the element performing this single flow.
     * @return The element performing this single flow
     */
    public Element getElement() {
        return elem;
    }
    
    /**
     * Gets the parent element thread.
     * @return The parent element thread.
     */
	public WorkThread getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.iis.simulation.Identifiable#getIdentifier()
	 */
	public int getIdentifier() {
		return id;
	}

	/**
	 * Sets the counter used to generate single flows' identifiers. 
	 * @param counter The new counter.
	 */
	public static void setCounter(int counter) {
		WorkThread.counter = counter;
	}
	
	/**
	 * Returns the current counter used to generate single flows' identifiers.
	 * @return The single flows' counter
	 */
	public static int getCounter() {
		return counter;
	}

    /**
     * Returns the priority of the element owner of this flow
     * @return The priority of the associated element.
     */
    public int getPriority() {
    	return elem.getPriority();
    }

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WT" + id + "(" + elem + ")\tFLOW: " + currentFlow;
	}
	
	@Override
	public int compareTo(WorkThread o) {
		if (id > o.getIdentifier())
			return 1;
		if (id < o.getIdentifier())
			return -1;
		return 0;
	}
	
	/**
	 * Returns a new instance of the element's main work thread. The element's main work thread is
	 * valid and has no parent.
	 * @param elem Element owner of this thread
	 * @return A new instance of the element's main work thread
	 */
	public static WorkThread getInstanceMainWorkThread(Element elem) {
		return new WorkThread(new WorkToken(true), elem, elem.getFlow(), null);
	}

	/**
	 * Returns a new instance of a work thread created to carry out the inner subflow of a structured flow. 
	 * The current thread is the parent of the newly created child thread. has the same state than the c
	 * @return A new instance of a work thread created to carry out the inner subflow of a structured flow
	 */
	public WorkThread getInstanceDescendantWorkThread(InitializerFlow newFlow) {
		if (isExecutable())
			return new WorkThread(new WorkToken(true), elem, newFlow, this);
		else
			return new WorkThread(new WorkToken(false, newFlow), elem, newFlow, this);
	}

	/**
	 * Returns a new instance of a work thread created to carry out a new flow after a split
	 * @param executable Indicates if the thread to be created has to be valid or not
	 * @param prevFlow The previously visited flow
	 * @param token The token to be cloned in case this work thread is not valid and the token is also not valid. 
	 * @return A new instance of a work thread created to carry out a new flow after a split
	 */
	public WorkThread getInstanceSubsequentWorkThread(boolean executable, Flow newFlow, WorkToken token) {
		WorkToken newToken;
		if (!executable)
			if (!token.isExecutable())
				newToken = new WorkToken(token);
			else
				newToken = new WorkToken(false, newFlow);
		else
			newToken = new WorkToken(true);
		return new WorkThread(newToken, elem, newFlow, parent);
	}

	/**
	 * Adds a new visited flow to the list.
	 * @param flow New visited flow
	 */
	public void updatePath(Flow flow) {
		token.addFlow(flow);
	}

	/**
	 * Returns this thread's current token.
	 * @return This thread's current token
	 */
	public WorkToken getToken() {
		return token;
	}
	
	/**
	 * Returns true if the specified flow was already visited from this thread.
	 * @param flow Flow to be checked.
	 * @return True if the specified flow was already visited from this thread; false otherwise.
	 */
	public boolean wasVisited (Flow flow) {
		return token.wasVisited(flow);
	}

	/**
	 * Returns the order this thread occupies among the rest of work threads.
	 * @return the order of arrival of this work thread to request the activity
	 */
	public int getArrivalOrder() {
		return arrivalOrder;
	}

	/**
	 * Sets the order this thread occupies among the rest of work threads.
	 * @param arrivalOrder the order of arrival of this work thread to request the activity
	 */
	public void setArrivalOrder(int arrivalOrder) {
		this.arrivalOrder = arrivalOrder;
	}

	/**
	 * Returns the timestamp when this work thread arrives to request the current single flow.
	 * @return the timestamp when this work thread arrives to request the current single flow
	 */
	public long getArrivalTs() {
		return arrivalTs;
	}

	/**
	 * Sets the timestamp when this work thread arrives to request the current single flow.
	 * @param arrivalTs the timestamp when this work thread arrives to request the current single flow
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
     * Returns the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 * @return the workgroup which is used to perform this flow, or <code>null</code>  
     * if the flow has not been carried out.
	 */
	public ActivityWorkGroup getExecutionWG() {
		return executionWG;
	}

	/**
	 * When the single flow can be carried out, sets the workgroup used to
	 * carry out the activity.
	 * @param executionWG the workgroup which is used to carry out this flow.
	 */
	public void setExecutionWG(ActivityWorkGroup executionWG) {
		this.executionWG = executionWG;
	}

    /**
     * Catch the resources needed for each resource type to carry out an activity.
     * @return The minimum availability timestamp of the taken resources 
     */
	public long acquireResources(ArrayDeque<Resource> caughtResources, int id) {
		if (this.caughtResources.containsKey(id))
			elem.error("Trying to assign group of resources to already occupied group when catching. ID:" + id);
		this.caughtResources.put(id, caughtResources);
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
    public Collection<Resource> releaseResources(int id) {
        return caughtResources.remove(id);
    }

	@Override
	public boolean equals(Object o) {
		if (((WorkThread)o).id == id)
			return true;
		return false;
	}


}
