/**
 * 
 */
package es.ull.iis.simulation.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeSet;

import es.ull.iis.function.TimeFunctionParams;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.model.engine.ElementInstanceEngine;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.Flow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.ReleaseResourcesFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.simulation.model.flow.TaskFlow;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.RandomPermutation;

/**
 * Represents an instance of an element, so multiple instances of the same element can be active at
 * the same time.
 * There are three types of element instances, each one requiring a different method to be created:
 * <ol>
 * <li>Main instance. The element's main instance. Must be created by invoking the static method
 * {@link #getMainElementInstance(Element)}</li>
 * <li>Descendant thread</li>A thread created to carry out the inner flows of a structured flow.
 * To invoke, use: {@link #getDescendantElementInstance(InitializerFlow)}</li>
 * <li>Subsequent thread</li>A thread created to carry out a new flow after a split.
 * To invoke, use: {@link #getSubsequentElementInstance(boolean, Flow, WorkToken)}</li>
 * </ol><p>
 *  An instance has an associated token, which can be true or false. A false token is used
 *  only for synchronization purposes and doesn't execute task flows. 
 * @author Ivan Castilla Rodriguez
 *
 */
public class ElementInstance implements TimeFunctionParams, Prioritizable, Comparable<ElementInstance>, Identifiable {
    /** Element which carries out this flow. */    
    private final Element elem; 
    /** The parent element thread */
    protected final ElementInstance parent;
    /** The descendant work threads */
	protected final ArrayList<ElementInstance> descendants;
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
	/** The arrival order of this work thread relatively to the rest of work threads 
	 * in the same activity manager. */
	protected int arrivalOrder;
	/** The simulation timestamp when this work thread was requested. */
	protected long arrivalTs = -1;
	/** The proportion of time left to finish the activity. Used in interruptible activities. */
	protected double remainingTask = 0.0;
	final private ElementInstanceEngine engine;
	
    /** 
     * Creates a new work thread. The constructor is private since it must be invoked from the 
     * <code>getInstance...</code> methods.
     * @param token An object containing the state of the thread  
     * @param elem Element owner of this thread
     * @param initialFlow The first flow to be executed by this thread
     * @param parent The parent thread, if this thread is included within a structured flow
     */
    private ElementInstance(WorkToken token, Element elem, Flow initialFlow, ElementInstance parent) {
    	this.token = token;
        this.elem = elem;
        this.parent = parent;
        descendants = new ArrayList<ElementInstance>();
        if (parent != null)
        	parent.addDescendant(this);
        this.initialFlow = initialFlow;
        this.engine = elem.getEngine().getElementInstance(this);
    }

    /**
	 * @return the engine
	 */
	public ElementInstanceEngine getEngine() {
		return engine;
	}

	@Override
	public int getIdentifier() {
		return engine.getIdentifier();
	}
	
	/**
     * Returns the priority of the element owner of this flow
     * @return The priority of the associated element.
     */
    @Override
    public int getPriority() {
    	return elem.getPriority();
    }

	/**
	 * Sets the flow currently executed by this FlowExecutor 
	 * @param f The flow to be performed
	 */
	public void setCurrentFlow(Flow f) {
    	currentFlow = f;
		executionWG = null;
		arrivalTs = -1;
		if (f instanceof RequestResourcesFlow) {
			remainingTask = 1.0;
			if (parent.currentFlow instanceof ActivityFlow) {
				if (parent.remainingTask > 0.0)
					remainingTask = parent.remainingTask;
			}
		}
		else {
			remainingTask = 0.0;  			
		}
	}

    /**
     * Returns the flow being performed.
	 * @return The flow being performed.
	 */
	public Flow getCurrentFlow() {
		return currentFlow;
	}
	    
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
	public void addDescendant(ElementInstance wThread) {
		descendants.add(wThread);
	}

	/**
	 * Removes a thread from the list of descendants. If it's the last thread of an element,
	 * the element has to be notified and finished.
	 * @param wThread Descendant thread
	 */
	public void removeDescendant(ElementInstance wThread) {
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
	 * Returns the last flow visited by this FlowExecutor
	 * @return the last flow visited by this FlowExecutor
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
	public ElementInstance getParent() {
		return parent;
	}

	/**
	 * Returns the main instance of the element. This is a "valid" instance and has no parent.
	 * @param elem Element owner of this instance
	 * @return the main instance of the element
	 */
	public static ElementInstance getMainElementInstance(Element elem) {
		return new ElementInstance(new WorkToken(true), elem, elem.getFlow(), null);
	}

	/**
	 * Returns a new instance of an element which carries out the inner subflow of a structured flow. 
	 * The current instance is the parent of the newly created child instance. 
	 * @return A new instance of an element created to carry out the inner subflow of a structured flow
	 */
	public ElementInstance getDescendantElementInstance(InitializerFlow newFlow) {
		assert isExecutable() : "Invalid parent to create descendant work thread"; 
		return new ElementInstance(new WorkToken(true), elem, newFlow, this);
	}

	/**
	 * Returns a new instance of an element which carries out a new flow after a split flow
	 * @param executable Indicates if the instance to be created has to be valid or not
	 * @param newFlow The flow associated to the new instance 
	 * @param token The token to be cloned in case the current instance is not valid and the token is also not valid. 
	 * @return A new instance of an element created to carry out a new flow after a split flow
	 */
	public ElementInstance getSubsequentElementInstance(boolean executable, Flow newFlow, WorkToken token) {
		final WorkToken newToken;
		if (!executable)
			if (!token.isExecutable())
				newToken = new WorkToken(token);
			else
				newToken = new WorkToken(false, newFlow);
		else
			newToken = new WorkToken(true);
		return new ElementInstance(newToken, elem, newFlow, parent);
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

	@Override
	public double getTime() {
		return elem.getTs();
	}

    /**
     * Catch the resources needed for each resource type to carry out an activity.
	 * @param solution Tentative solution with booked resources
     * @return The minimum availability timestamp of the taken resources 
     */
	public long catchResources(ArrayDeque<Resource> solution) {
		final RequestResourcesFlow reqFlow = (RequestResourcesFlow)currentFlow;
		// Add booked resources to the element
		elem.seizeResources(reqFlow, this, solution);
    	long auxTs = Long.MAX_VALUE;
    	for (Resource res : solution) {
    		auxTs = Math.min(auxTs, res.catchResource(this));;
            res.getCurrentResourceType().debug("Resource taken\t" + res + "\t" + getElement());
    	}
    	engine.notifyResourcesAcquired();
    	final long ts = elem.getTs();
		elem.getSimulation().notifyInfo(new ElementActionInfo(elem.getSimulation(), this, elem, reqFlow, executionWG, solution, ElementActionInfo.Type.ACQ, ts));
		elem.debug("Resources acquired\t" + this + "\t" + reqFlow.getDescription());			
		reqFlow.afterAcquire(this);
		long delay = Math.round(executionWG.getDurationSample(this) * remainingTask);
		auxTs -= ts;
		if (delay > 0) {
			if (remainingTask == 1.0) {
				elem.getSimulation().notifyInfo(new ElementActionInfo(elem.getSimulation(), this, elem, reqFlow, executionWG, null, ElementActionInfo.Type.START, ts));
				elem.debug("Start delay\t" + this + "\t" + reqFlow.getDescription());
			}
			else {
				elem.getSimulation().notifyInfo(new ElementActionInfo(elem.getSimulation(), this, elem, reqFlow, executionWG, null, ElementActionInfo.Type.RESACT, ts));
				elem.debug("Continues\t" + this + "\t" + reqFlow.getDescription());			
			}
			// The required time for finishing the activity is reduced (useful only for interruptible activities)
			if (reqFlow.partOfInterruptible() && (delay - auxTs > 0.0)) {
				remainingTask = (delay - auxTs) * remainingTask / (double)delay;
				delay = auxTs;
			}
			else {
				remainingTask = 0.0;
			}
		}
		else {
			remainingTask = 0.0;
		}
		return delay;
	}
	
	public void startDelay(long delay) {
		elem.addFinishEvent(delay + elem.getTs(), (TaskFlow)currentFlow, this);
	}
	
	/**
	 * Releases the previously seized resources
	 * @param wg Workgroup of resources to release
	 * @return The released resources
	 */
	public ArrayDeque<Resource> releaseCaughtResources(WorkGroup wg) {
        final TreeSet<ActivityManager> amList = new TreeSet<ActivityManager>();
		final ReleaseResourcesFlow relFlow = (ReleaseResourcesFlow)currentFlow;
		
		final ArrayDeque<Resource> resources = elem.releaseResources(relFlow, this, wg);
        // Generate unavailability periods.
        for (Resource res : resources) {
        	final long cancellationDuration = relFlow.getResourceCancellation(res.getCurrentResourceType(), this);
        	if (cancellationDuration > 0) {
				final long actualTs = elem.getTs();
				res.setNotCanceled(false);
				elem.getSimulation().notifyInfo(new ResourceInfo(elem.getSimulation(), res, res.getCurrentResourceType(), ResourceInfo.Type.CANCELON, actualTs));
				res.generateCancelPeriodOffEvent(actualTs, cancellationDuration);
			}
			elem.debug("Returned " + res);
        	// The resource is freed
        	if (res.releaseResource(this)) {
        		// The activity managers involved are included in the list
        		for (ActivityManager am : res.getCurrentManagers()) {
        			amList.add(am);
        		}
        	}
        }
		if (Simulation.isRandomNotifyAMs()) {
			final int[] order = RandomPermutation.nextPermutation(amList.size());
			ActivityManager[] ams = new ActivityManager[amList.size()];
			ams = (ActivityManager[]) amList.toArray(ams);
			for (int ind : order) {
				ActivityManager am = ams[ind];
				am.notifyResource();
			}
		}
		else {
			for (ActivityManager am : amList) {
				am.notifyResource();
			}
		}
        return resources;
	}
   
    public void endDelay(RequestResourcesFlow f) {
		if (remainingTask == 0.0) {
			elem.getSimulation().notifyInfo(new ElementActionInfo(elem.getSimulation(), this, elem, f, executionWG, null, ElementActionInfo.Type.END, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + f.getDescription());
			f.afterFinalize(this);
		}
		else {
			elem.getSimulation().notifyInfo(new ElementActionInfo(elem.getSimulation(), this, elem, f, executionWG, null, ElementActionInfo.Type.INTACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes part of \t" + this + "\t" + f.getDescription() + "\t" + remainingTask * 100 + "% Left");
			// Notifies the parent workthread that the activity was interrupted
			parent.remainingTask = remainingTask;
		}
    }

    public boolean wasInterrupted(ActivityFlow f) {
		// It was an interruptible activity and it was interrupted
		return (remainingTask > 0.0);    	
    }

	@Override
	public int compareTo(ElementInstance o) {
		final int id1 = engine.getIdentifier();
		final int id2 = o.engine.getIdentifier();
		if (id1 > id2)
			return 1;
		if (id2 < id1)
			return -1;
		return 0;
	}
    
}
