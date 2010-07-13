package es.ull.isaatc.simulation.sequential3Phase;

import java.util.ArrayList;

import es.ull.isaatc.simulation.common.Identifiable;
import es.ull.isaatc.simulation.sequential3Phase.flow.BasicFlow;
import es.ull.isaatc.simulation.sequential3Phase.flow.TaskFlow;
import es.ull.isaatc.simulation.sequential3Phase.flow.Flow;
import es.ull.isaatc.simulation.sequential3Phase.flow.InitializerFlow;
import es.ull.isaatc.simulation.sequential3Phase.flow.SingleFlow;
import es.ull.isaatc.util.Prioritizable;

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
 * @author Iván Castilla Rodríguez
 */
public class WorkThread implements Identifiable, Prioritizable, Comparable<WorkThread> {
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
	/** Thread's current Work Item */
	protected final WorkItem wItem;
	/** A flag to indicate if the thread executes the flow or not */
	protected WorkToken token;
	/** The current flow the thread is in */
	protected BasicFlow currentFlow = null;
	/** The last flow the thread was in */
	protected Flow lastFlow = null;
    
    /** 
     * Creates a new parent single flow which wraps an activity. 
     * @param elem Element that executes this flow.
     * @param act Activity wrapped with this flow.
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
        wItem = new WorkItem(this);
    }

    public void setCurrentFlow(Flow f) {
    	currentFlow = (BasicFlow)f;
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
	 * @param e
	 * @return
	 * @see java.util.TreeSet#add(java.lang.Object)
	 */
	public void addDescendant(WorkThread wThread) {
		descendants.add(wThread);
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.TreeSet#remove(java.lang.Object)
	 */
	public void removeDescendant(WorkThread wThread) {
		descendants.remove(wThread);
		if (parent == null && descendants.size() == 0)
			elem.notifyEnd();
	}

	/**
	 * @return the executable
	 */
	public boolean isExecutable() {
		return token.isExecutable();
	}

	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(boolean executable) {
		token = new WorkToken(executable);
	}
	
	/**
	 * @param executable the executable to set
	 */
	public void setExecutable(boolean executable, Flow startPoint) {
		if (!executable)
			token = new WorkToken(executable, startPoint);
		else
			token = new WorkToken(executable);
	}

	/**
	 * @return the lastFlow
	 */
	public Flow getLastFlow() {
		return lastFlow;
	}

	/**
	 * @param lastFlow the lastFlow to set
	 */
	public void setLastFlow(Flow lastFlow) {
		this.lastFlow = lastFlow;
	}

	/**
     * Returns the element which carries out this flow.
     * @return The associated element.
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

	/**
	 * @return the wItem
	 */
	public WorkItem getNewWorkItem(SingleFlow sf) {
		wItem.reset(sf);
		return wItem;
	}

	/**
	 * @return the wItem
	 */
	public WorkItem getWorkItem() {
		return wItem;
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Identifiable#getIdentifier()
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
		return "SF" + id + "(" + elem + ")";
	}
	
	public int compareTo(WorkThread o) {
		if (id > o.id)
			return 1;
		if (id < o.id)
			return -1;
		return 0;
	}
	
	public static WorkThread getInstanceMainWorkThread(Element elem) {
		return new WorkThread(new WorkToken(true), elem, elem.getFlow(), null);
	}
	
	public WorkThread getInstanceDescendantWorkThread(InitializerFlow newFlow) {
		if (isExecutable())
			return new WorkThread(new WorkToken(true), elem, newFlow, this);
		else
			return new WorkThread(new WorkToken(false, newFlow), elem, newFlow, this);
	}

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

	public void updatePath(Flow flow) {
		token.addFlow(flow);
	}

	public WorkToken getToken() {
		return token;
	}
	
	public boolean wasVisited (Flow flow) {
		return token.wasVisited(flow);
	}

}
