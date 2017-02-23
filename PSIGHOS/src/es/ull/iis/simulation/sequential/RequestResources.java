/**
 * 
 */
package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;
import java.util.Iterator;

import es.ull.iis.simulation.model.QueuedObject;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.PrioritizedTable;

/**
 * @author Iván Castilla
 *
 */
public class RequestResources extends VariableStoreSimulationObject implements Prioritizable, QueuedObject<WorkThread>, ResourceHandler {
    /** Total of work items waiting for carrying out this activity */
    protected int queueSize = 0;
    /** Activity manager this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Groups available to perform this basic step */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** Indicates that the basic step is potentially feasible. */
    protected boolean stillFeasible = true;

    final protected RequestResourcesFlow modelReq;
	/**
	 * @param simul
	 * @param description
	 */
	public RequestResources(SequentialSimulationEngine simul, RequestResourcesFlow modelReq) {
		super(simul.getNextActivityId(), simul, "REQ");
		this.modelReq = modelReq;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
        for (int i = 0; i < modelReq.getWorkGroupSize(); i++) {
        	es.ull.iis.simulation.model.ActivityWorkGroup modelAWG = modelReq.getWorkGroup(i);
        	workGroupTable.add(new ActivityWorkGroup(simul, modelAWG));
        }
        simul.add(this);
	}

	public RequestResourcesFlow getModelReqFlow() {
		return modelReq;
	}

	public String getDescription() {
		return modelReq.getDescription();
	}

	@Override
    public int getPriority() {
        return modelReq.getPriority();
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
	
	public int getWorkGroupSize() {
		return workGroupTable.size();
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
	protected ArrayDeque<ResourceEngine> isFeasible(WorkThread wt) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<ResourceEngine> solution = wg.isFeasible(wt); 
            if (solution != null) {
                wt.setExecutionWG(wg);
        		wt.getElement().debug("Can carry out \t" + this + "\t" + wt.getModelWG());
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

    /**
     * Add a work thread to the element queue.
     * @param wt Work thread added
     */
    public void queueAdd(WorkThread wt) {
        manager.queueAdd(wt);
    	queueSize++;
		wt.getElement().incInQueue(wt);
		modelReq.inqueue(wt);
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
}
