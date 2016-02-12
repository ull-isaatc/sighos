package es.ull.iis.simulation.parallel;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.iis.simulation.core.Identifiable;
import es.ull.iis.simulation.parallel.flow.BasicFlow;
import es.ull.iis.simulation.parallel.flow.Flow;
import es.ull.iis.simulation.parallel.flow.SingleFlow;
import es.ull.iis.simulation.parallel.flow.TaskFlow;
import es.ull.isaatc.util.Prioritizable;

/**
 * A sequential branch of activities in an element's flow. Represents an element instance, so
 * multiple instances can be active at the same time.<p>
 * There are three types of Work threads which require a different method to be created:
 * <ol>
 * <li>Main thread. Is the element's main thread. Must be created by invoking the static method
 * {@link #getInstanceMainWorkThread(Element)}</li>
 * <li>Descendant thread</li>A thread created to carry out the inner flows of a structured flow.
 * To invoke, use: {@link #getInstanceDescendantWorkThread()}</li>
 * <li>Subsequent thread</li>A thread created to carry out a new flow after a split.
 * To invoke, use: {@link #getInstanceSubsequentWorkThread(boolean, Flow, WorkToken)}</li>
 * </ol><p>
 *  A work thread has an associated token, which can be true or false. A false token is used
 *  only for synchronization purposes and doesn't execute task flows. 
 * @author Iván Castilla Rodríguez
 */
public class WorkThread implements Identifiable, Prioritizable, Comparable<WorkThread> {
	/** Thread's Counter. Useful for identifying each single flow */
	private static AtomicInteger counter = new AtomicInteger();
	/** Thread's internal identifier */
	private final int id;
    /** Element owner of this thread. */    
    private final Element elem; 
    /** The parent element thread */
    private final WorkThread parent;
    /** The descendant work threads */
	private final ArrayList<WorkThread> descendants = new ArrayList<WorkThread>();
	/** Thread's current Work Item */
	private final WorkItem wItem;
	/** A flag to indicate if the thread executes the flow or not */
	private WorkToken token;
	/** The current flow the thread is in */
	protected BasicFlow currentFlow = null;
	/** The last flow the thread was in */
	private Flow lastFlow = null;
    
    /** 
     * Creates a new work thread. The constructor is private since it must be invoked from the 
     * <code>getInstance...</code> methods.
     * @param token An object containing the state of the thread  
     * @param elem Element owner of this thread
     * @param initialFlow The first flow to be executed by this thread
     * @param parent The parent thread, if this thread is included within a structured flow
     */
    private WorkThread(WorkToken token, Element elem, WorkThread parent) {
    	this.token = token;
        this.elem = elem;
        this.parent = parent;
        if (parent != null)
        	parent.addDescendant(this);
        this.id = counter.getAndIncrement();
        wItem = new WorkItem(this);
    }

    public void requestFlow(Flow f) {
    	currentFlow = (BasicFlow)f;
    	currentFlow.request(this);
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
	 * Returns the last flow visited by this thread.
	 * @return The last flow visited by this thread
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
     * Returns the element owner of this thread.
     * @return The element owner of this thread
     */
    public Element getElement() {
        return elem;
    }
    
    /**
     * Returns the parent thread.
     * @return The parent thread.
     */
	public WorkThread getParent() {
		return parent;
	}

	/**
	 * Gets a new work item to carry out a single flow. 
	 * @return The new work item
	 */
	public WorkItem getNewWorkItem(SingleFlow sf) {
		wItem.reset(sf);
		return wItem;
	}

	/**
	 * Returns the current work item.
	 * @return The current work item
	 */
	public WorkItem getWorkItem() {
		return wItem;
	}

	@Override
	public int getIdentifier() {
		return id;
	}

	/**
	 * Sets the counter used to generate work threads' identifiers. 
	 * @param counter The new counter.
	 */
	public static void setCounter(int counter) {
		WorkThread.counter.set(counter);
	}
	
	/**
	 * Returns the current counter used to generate work threads' identifiers.
	 * @return The work threads' current counter
	 */
	public static int getCounter() {
		return counter.get();
	}

    /**
     * Returns the priority of the element owner of this thread.
     * @return The priority of the element owner of this thread
     */
	@Override
    public int getPriority() {
    	return elem.getPriority();
    }

	@Override
	public String toString() {
		return "WT" + id + "(" + elem + ")";
	}
	
	@Override
	public int compareTo(WorkThread o) {
		final int id1 = id;
		final int id2 = o.id;
		if (id1 > id2)
			return 1;
		if (id1 < id2)
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
		return new WorkThread(new WorkToken(true), elem, null);
	}
	
	/**
	 * Returns a new instance of a work thread created to carry out the inner subflow of a structured flow. 
	 * The current thread is the parent of the newly created child thread. has the same state than the c
	 * @return A new instance of a work thread created to carry out the inner subflow of a structured flow
	 */
	public WorkThread getInstanceDescendantWorkThread() {
		assert isExecutable() : "Invalid parent to create descendant work thread"; 
		return new WorkThread(new WorkToken(true), elem, this);
	}

	/**
	 * Returns a new instance of a work thread created to carry out a new flow after a split
	 * @param executable Indicates if the thread to be created has to be valid or not
	 * @param prevFlow The previously visited flow
	 * @param token The token to be cloned in case this work thread is not valid and the token is also not valid. 
	 * @return A new instance of a work thread created to carry out a new flow after a split
	 */
	public WorkThread getInstanceSubsequentWorkThread(boolean executable, Flow prevFlow, WorkToken token) {
		final WorkToken newToken;
		if (!executable)
			if (!token.isExecutable())
				newToken = new WorkToken(token);
			else
				newToken = new WorkToken(false, prevFlow);
		else
			newToken = new WorkToken(true);
		return new WorkThread(newToken, elem, parent);
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

}
