package es.ull.isaatc.simulation;

import java.util.Iterator;

import es.ull.isaatc.function.RandomFunction;
import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.random.RandomNumber;
import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.util.*;

/**
 * A task which could be carry out by an element. An activity is characterized by its priority,
 * presentiality, and a set of workgropus. Each workgroup represents a combination of resource 
 * types required for carrying out the activity, and the duration of the activity when performed
 * with this workgroup. 
 * @author Carlos Martín Galán
 */
public class Activity extends DescSimulationObject implements Prioritizable, RecoverableState<ActivityState> {
    /** Priority of the activity */
    protected int priority = 0;
    /** Indicates if the activity is presential (an element carrying out this activity could
     * not make anything else) or not presential (the element could perform other activities at
     * the same time) */
    protected boolean presential = true;
    /** This queue contains the single flows that are waiting for this activity */
    protected RemovablePrioritizedTable<SingleFlow> queue;
    /** Activity manager which this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Group Pool */
    protected NonRemovablePrioritizedTable<WorkGroup> workGroupTable;

    /**
     * Creates a new activity.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0, true);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public Activity(int id, Simulation simul, String description, int priority) {
        this(id, simul, description, priority, true);
    }
    
    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param presential Indicates if the activity requires the presence of an element to be carried out. 
     */
    public Activity(int id, Simulation simul, String description, boolean presential) {
        this(id, simul, description, 0, presential);
    }
    
    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param presential Indicates if the activity requires the presence of an element to be carried out. 
     */
    public Activity(int id, Simulation simul, String description, int priority, boolean presential) {
        super(id, simul, description);
        this.priority = priority;
        this.presential = presential;
        queue = new RemovablePrioritizedTable<SingleFlow>();
        workGroupTable = new NonRemovablePrioritizedTable<WorkGroup>();
    }

    /**
     * Indicates if the activity requires the presence of the element in order to be carried out. 
     * @return The "presenciality" of the activity.
     */
    public boolean isPresential() {
        return presential;
    }
    
    /**
     * Returns the activity's priority.
     * @return Value of the activity's priority.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the activity manager which this activity belongs to.
     * @return The activity manager which this activity belongs to.
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
     * Creates a new workgroup for this activity. The workgroup is added and returned in order
     * to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, TimeFunction duration, int priority) {
    	WorkGroup wg = new WorkGroup(wgId, this, duration, priority);
        workGroupTable.add(wg);
        return wg;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. The workgroup 
     * is added and returned in order to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, TimeFunction duration) {    	
        return getNewWorkGroup(wgId, duration, 0);
    }

    /**
     * Creates a new workgroup for this activity. The workgroup is added and returned in order
     * to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration, int priority) {
    	WorkGroup wg = new WorkGroup(wgId, this, new RandomFunction(duration), priority);
        workGroupTable.add(wg);
        return wg;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. The workgroup 
     * is added and returned in order to be used.
     * @param wgId The workgroup's identifier
     * @param duration Duration of the activity when performed with the new workgroup
     * @return A new workgroup.
     */
    public WorkGroup getNewWorkGroup(int wgId, RandomNumber duration) {    	
        return getNewWorkGroup(wgId, duration, 0);
    }

    /**
     * Returns an iterator over the workgroups of this activity.
     * @return An iterator over the workgroups that can perform this activity.
     */
    public Iterator<WorkGroup> iterator() {
    	return workGroupTable.iterator(NonRemovablePrioritizedTable.IteratorType.FIFO);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public WorkGroup getWorkGroup(Integer wgId) {
        Iterator<WorkGroup> iter = workGroupTable.iterator(NonRemovablePrioritizedTable.IteratorType.FIFO);
        while (iter.hasNext()) {
        	WorkGroup opc = iter.next();
        	if (opc.compareTo(wgId) == 0)
        		return opc;        	
        }
        return null;
    }
    
	/**
     * Checks if this activity can be performed with any of its workgroups.
     * @param sf Single flow which contains the activity 
     * @return The workgroup the activity can be performed with. null if the activity isn't feasible.
     */
    protected WorkGroup isFeasible(SingleFlow sf) {
        Iterator<WorkGroup> iter = workGroupTable.iterator(NonRemovablePrioritizedTable.IteratorType.RANDOM);
        while (iter.hasNext()) {
        	WorkGroup opc = iter.next();
            if (opc.isFeasible(sf)) {
                sf.setExecuting(true);
                if (isPresential())
                	sf.getElement().setCurrentSF(sf);
                return opc;
            }            
        }
        return null;
    }

	/**
	 * Checks if there are "valid" elements waiting for this activity in the activity queue, and 
	 * returns the first valid element. An element is valid when it's not busy carrying out 
	 * another activity.
	 * @return null if there are no pending elements; the first valid element in other case. 
	 */
    protected SingleFlow hasPendingElements() {
    	Iterator<SingleFlow> iter = queue.iterator(RemovablePrioritizedTable.IteratorType.FIFO);
    	while (iter.hasNext()) {
    		SingleFlow sf = iter.next();
            Element e = sf.getElement();
    		e.debug("MUTEX\trequesting\t" + this + " (has el.)");    	
            e.waitSemaphore();
    		e.debug("MUTEX\tadquired\t" + this + " (has el.)");    	
            
            // MOD 26/01/06 Añadido
            e.setTs(getTs());
            if ((e.getCurrentSF() == null) || !presential)
                return sf;
            else {
        		e.debug("MUTEX\treleasing\t" + this + " (has el.)");    	
            	e.signalSemaphore();
        		e.debug("MUTEX\tfreed\t" + this + " (has el.)");    	
            }
    	}
    	return null;
    }

    /**
     * Add a single flow to the element queue.
     * @param flow Single Flow added
     */
    protected void queueAdd(SingleFlow flow) {
        queue.add(flow);
    }
    
    /**
     * Remove a specific single flow from the element queue.
     * @param flow Single flow that must be removed from the element queue.
     * @return True if the flow belongs to the queue; false in other case.
     */
    protected boolean queueRemove(SingleFlow flow) {
        return queue.remove(flow);
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	@Override
	public double getTs() {
		return manager.getTs();
	}

	/**
	 * Gets the state of this activity. The state of an activity consists on the queue of single flows.
	 * @return This activity's state
	 */
	public ActivityState getState() {
		ActivityState state = new ActivityState(id);
    	Iterator<SingleFlow> iter = queue.iterator(RemovablePrioritizedTable.IteratorType.FIFO);
    	while (iter.hasNext()) {
    		SingleFlow sf = iter.next();
			state.add(sf.getIdentifier(), sf.getElement().getIdentifier());    		
    	}
		return state;
	}

	/**
	 * Sets the state of this activity. The state of an activity consists on the queue of single flows.
	 * @param state The activity's state.
	 */
	public void setState(ActivityState state) {
		for (ActivityState.ActivityQueueEntry aqe : state.getQueue()) {
			Element elem = simul.getActiveElement(aqe.getElemId());
			queue.add(elem.searchSingleFlow(aqe.getFlowId()));
		}		
	}

}
