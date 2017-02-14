/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import java.util.ArrayDeque;
import java.util.Iterator;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.QueuedObject;
import es.ull.iis.simulation.core.flow.Flow;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.sequential.ActivityManager;
import es.ull.iis.simulation.sequential.ActivityWorkGroup;
import es.ull.iis.simulation.sequential.Element;
import es.ull.iis.simulation.sequential.Resource;
import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkGroup;
import es.ull.iis.simulation.core.WorkThread;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.PrioritizedTable;

/**
 * @author Iván Castilla
 *
 */
public class RequestResourcesFlow extends SingleSuccessorFlow implements es.ull.iis.simulation.core.flow.RequestResourcesFlow, Prioritizable, QueuedObject<WorkThread> {
    /** Priority. The lowest the value, the highest the priority */
    protected final int priority;
    /** A brief description of the activity */
    protected final String description;
    /** Total of work items waiting for carrying out this activity */
    protected int queueSize = 0;
    /** Activity manager this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Groups available to perform this basic step */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** Indicates that the basic step is potentially feasible. */
    protected boolean stillFeasible = true;
    /** A unique identifier that serves to tell a ReleaseResourcesFlow which resources to release */
	protected final int resourcesId;

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Simulation simul, String description, int resourcesId) {
		this(simul, description, resourcesId, 0);
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public RequestResourcesFlow(Simulation simul, String description, int resourcesId, int priority) {
		super(simul);
        this.description = description;
        this.priority = priority;
		this.resourcesId = resourcesId;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
        // TODO: Check if it's needed because BasicFlow is already using simul.add(Flow...) 
        simul.add(this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
    public int getPriority() {
        return priority;
    }
	
    /**
     * Returns the activity manager this activity belongs to.
     * @return The activity manager this activity belongs to.
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Sets the activity manager this activity type belongs to. It also
     * adds this activity to the manager.
     * @param manager The activity manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
	/**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity using the specified wg. This workgroup
     * is only available if cond is true.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(this, wgId, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg, Condition cond) {    	
        return addWorkGroup(0, wg, cond);
    }

    /**
     * Returns an iterator over the workgroups of this activity.
     * @return An iterator over the workgroups that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}
	
	@Override
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				final Element elem = (Element)wThread.getElement();
				if (beforeRequest(elem)) {
					simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.REQACT, elem.getTs()));
					if (elem.isDebugEnabled())
						elem.debug("Requests\t" + this + "\t" + description);
					if (validElement(wThread)) {
						// There are enough resources to perform the activity
						final ArrayDeque<Resource> solution = isFeasible(wThread); 
						if (solution != null) {
							carryOut(wThread, solution);
						}
						else {
							queueAdd(wThread); // The element is introduced in the queue
						}
					} else {
						queueAdd(wThread); // The element is introduced in the queue
					}
				}
				else {
					wThread.setExecutable(false, this);
					next(wThread);
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}


	/**
	 * Catches the resources required to carry out this basic step. 
	 * @param wThread Work thread requesting this basic step
	 */
	protected void carryOut(WorkThread wThread, ArrayDeque<Resource> solution) {
		final Element elem = (Element)wThread.getElement();
		wThread.acquireResources(solution, resourcesId);
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(simul, wThread, ElementActionInfo.Type.STAACT, elem.getTs()));
		next(wThread);
	}
	
	/**
     * Checks if this basic step can be performed with any of its workgroups. Firstly 
     * checks if the basic step is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the basic step cannot be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param wt Work thread wanting to perform the basic step 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the basic step by using this workgroup.
     */
	protected ArrayDeque<Resource> isFeasible(WorkThread wt) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wt); 
            if (solution != null) {
                wt.setExecutionWG(wg);
        		wt.getElement().debug("Can carry out \t" + this + "\t" + wt.getExecutionWG());
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
	}

	/**
	 * Checks if the element is valid to perform this basic step.
	 * @param wThread Work thread requesting this basic step
	 * @return True if the element is valid, false in other case.
	 */
	protected boolean validElement(WorkThread wThread) {
		return true;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ACQ";
	}

    /**
     * Add a work thread to the element queue.
     * @param wt Work thread added
     */
    public void queueAdd(WorkThread wt) {
        manager.queueAdd(wt);
    	queueSize++;
		wt.getElement().incInQueue(wt);
		inqueue(wt);
    }
    
    /**
     * Remove a specific work item from the element queue.
     * @param wt Work Item that must be removed from the element queue.
     */
    public void queueRemove(WorkThread wt) {
    	manager.queueRemove(wt);
    	queueSize--;
		wt.getElement().decInQueue(wt);
    }

    /**
     * Returns the size of this activity's queue 
     * @return the size of this activity's queue
     */
    public int getQueueSize() {
    	return queueSize;    	
    }

    /**
     * Sets this activity as potentially feasible.
     */
    public void resetFeasible() {
    	stillFeasible = true;
    }
    
	/**
	 * Allows a user to add actions carried out when an element is enqueued in an Activity, 
	 * waiting for availables Resources. 
	 * @param e Element requesting this single flow
	 */
	@Override
	public void inqueue(WorkThread wt){};	
	
	@Override
	public void addPredecessor(Flow newFlow) {}

	@Override
	public void availableElement(WorkThread wThread) {
		ArrayDeque<Resource> solution = isFeasible(wThread);
		if (solution != null) {
			carryOut(wThread, solution);
			queueRemove(wThread);
		}
	}

	@Override
	public int availableResource(WorkThread wThread) {
        if (validElement(wThread)) {
        	ArrayDeque<Resource> solution = isFeasible(wThread); 
        	if (solution != null) {	// The activity can be performed
                carryOut(wThread, solution);
                return -1;
        	}
        	else {
        		return getQueueSize();
        	}
        }
        return 0;
		
	}
}
