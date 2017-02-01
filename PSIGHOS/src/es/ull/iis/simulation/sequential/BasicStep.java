package es.ull.iis.simulation.sequential;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.util.PrioritizedTable;

/**
 * A task which could be carried out by an element. An activity is characterized by its priority
 * and a set of workgropus. Each workgroup represents a combination of resource types required 
 * for carrying out the activity.<p>
 * Each activity belongs to an Activity Manager, which handles the way the activity is accessed.<p>
 * An activity is potentially feasible if there is no proof that there are not enough resources
 * to perform it. An activity is feasible if it's potentially feasible and there is at least one
 * workgroup with enough available resources to perform the activity.<p>
 * An activity can be requested by a valid element, that is, check if the activity is feasible. 
 * If the activity is not feasible, the element is added to a queue until new resources are 
 * available. If the activity is feasible, the element "carries out" the activity, that is, 
 * catches the resources needed to perform the activity. Whenever it is determined that the 
 * activity has finished, the element releases the resources previously caught.<p>
 * An activity can also define cancellation periods for each one of the resource types it uses. 
 * If an element takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * FIXME: Complete and rewrite (original description for TimeDrivenActivities)
 *  A task which could be carried out by an element in a specified time. This kind of activities
 * can be characterized by a priority value, presentiality, interruptibility, and a set of 
 * workgropus. Each workgroup represents a combination of resource types required for carrying out 
 * the activity, and the duration of the activity when performed with this workgroup.<p>
 * By default, time-driven activities are presential, that is, an element carrying out this 
 * activity can't perform simultaneously any other presential activity; and ininterruptible, i.e., 
 * once started, the activity keeps its resources until it's finished, even if the resources become 
 * unavailable while the activity is being performed. This two characteristics are customizable by 
 * means of the <code>Modifier</code> enum type. An activity can be <code>NONPRESENTIAL</code>, when 
 * the element can perform other activities while it's performing this one; and <code>INTERRUPTIBLE</code>, 
 * when the activity can be interrupted, and later continued, if the resources become unavailable 
 * while the activity is being performed.

 * @author Carlos Martín Galán
 */
public abstract class BasicStep extends TimeStampedSimulationObject implements es.ull.iis.simulation.core.BasicStep<ActivityWorkGroup, WorkThread, Resource> {
    /** Priority. The lowest the value, the highest the priority */
    protected int priority = 0;
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
    /** Resources cancellation table */
    protected final ArrayList<CancelListEntry> cancellationList;

	/**
     * Creates a new activity with 0 priority.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public BasicStep(Simulation simul, String description) {
        this(simul, description, 0);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public BasicStep(Simulation simul, String description, int priority) {
        super(simul.getNextActivityId(), simul);
        this.description = description;
        this.priority = priority;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
        simul.add(this);
		cancellationList = new ArrayList<CancelListEntry>();
    }

	@Override
	public String getDescription() {
		return description;
	}

	@Override
    public int getPriority() {
        return priority;
    }
    
	@Override
	public long getTs() {
		return manager.getTs();
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

    /**
     * Sets the activity as potentially feasible.
     */
    protected void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Add a work thread to the element queue.
     * @param wt Work thread added
     */
    protected void queueAdd(WorkThread wt) {
        manager.queueAdd(wt);
    	queueSize++;
		wt.getElement().incInQueue(wt);
		wt.getSingleFlow().inqueue(wt.getElement());
    }
    
    /**
     * Remove a specific work item from the element queue.
     * @param wt Work Item that must be removed from the element queue.
     */
    protected void queueRemove(WorkThread wt) {
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
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancelation(ResourceType rt, long duration) {
		CancelListEntry entry = new CancelListEntry(rt, duration);
		cancellationList.add(entry);
	}
	
	/** 
	 * Elements of the cancellation list.
	 * @author ycallero
	 *
	 */
	public class CancelListEntry {		
		public ResourceType rt;
		public long dur;
		
		CancelListEntry(ResourceType rt, long dur) {
			this.rt = rt;
			this.dur = dur;
		}
	}
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}

}
