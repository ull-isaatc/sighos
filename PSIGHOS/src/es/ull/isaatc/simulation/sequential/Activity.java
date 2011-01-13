package es.ull.isaatc.simulation.sequential;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.condition.TrueCondition;
import es.ull.isaatc.util.PrioritizedTable;

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
 * An activity can also defined cancellation periods for each one of the resource types it uses. 
 * If an element takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * @author Carlos Martín Galán
 */
public abstract class Activity extends TimeStampedSimulationObject implements es.ull.isaatc.simulation.core.Activity {
    /** Priority. The lowest the value, the highest the priority */
    protected int priority = 0;
    /** A brief description of the activity */
    protected final String description;
    /** Total of work items waiting for carrying out this activity */
    protected int queueSize = 0;
    /** Activity manager this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Groups available to perform this activity */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Resources cancellation table */
    protected final ArrayList<CancelListEntry> cancellationList;
    /** Last activity start */
    protected long lastStartTs = 0;
    /** Last activity finish */
    protected long lastFinishTs = 0;

	/**
     * Creates a new activity with 0 priority.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public Activity(int id, Simulation simul, String description, int priority) {
        super(id, simul);
        this.description = description;
        this.priority = priority;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
        simul.add(this);
		cancellationList = new ArrayList<CancelListEntry>();
    }

    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Describable#getDescription()
     */
	public String getDescription() {
		return description;
	}

	/**
     * Returns the activity's priority.
     * @return Value of the activity's priority.
     */
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
    
    public boolean isInterruptible() {
    	return false;
    }
    
	/** 
	 * Returns <tt>true</tt> if the activity is non presential, i.e., an element can perform other 
	 * activities at the same time. 
	 * @return <tt>True</tt> if the activity is non presential, <tt>false</tt> in other case.
	 */
	public boolean isNonPresential() {
		return true;
	}
	
	/**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, priority, wg));
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
        workGroupTable.add(new ActivityWorkGroup(wgId, priority, wg, cond));
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
     * Checks if this activity can be performed with any of its workgroups. Firstly 
     * checks if the activity is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the activity can't be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param wi Work Item wanting to perform the activity 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the activity by using this workgroup.
     */
    protected ArrayDeque<Resource> isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return null;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
        	ArrayDeque<Resource> solution = wg.isFeasible(wi); 
            if (solution != null) {
                wi.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wi.getExecutionWG());
                return solution;
            }            
        }
        stillFeasible = false;
        return null;
    }

    /**
     * Sets the activity as potentially feasible.
     */
    protected void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Add a work item to the element queue.
     * @param wi Work Item added
     */
    protected void queueAdd(WorkItem wi) {
        manager.queueAdd(wi);
    	queueSize++;
		wi.getElement().incInQueue(wi);
		wi.getFlow().inqueue(wi.getElement());
    }
    
    /**
     * Remove a specific work item from the element queue.
     * @param wi Work Item that must be removed from the element queue.
     */
    protected void queueRemove(WorkItem wi) {
    	manager.queueRemove(wi);
    	queueSize--;
		wi.getElement().decInQueue(wi);
    }

    /**
     * Returns the size of this activity's queue 
     * @return the size of this activity's queue
     */
    public int getQueueSize() {
    	return queueSize;    	
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	@Override
	public long getTs() {
		return manager.getTs();
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
	
	/**
	 * Checks if the element is valid to perform this activity.
	 * @param wItem Work item requesting this activity
	 * @return True if the element is valid, false in other case.
	 */
	public abstract boolean validElement(WorkItem wItem);
	
	/**
	 * Requests this activity. Checks if this activity is feasible by the
	 * specified work item. If the activity is feasible, <code>carryOut</code>
	 * is called; in other case, the work item is added to this activity's queue.
	 * @param wItem Work Item requesting this activity.
	 */
	public abstract void request(WorkItem wItem);

	/**
	 * Catches the resources required to carry out this activity.
	 * @param wItem Work item requesting this activity
	 */
	public abstract void carryOut(WorkItem wItem, ArrayDeque<Resource> solution);

	/**
	 * Releases the resources required to carry out this activity.
	 * @param wItem Work item which requested this activity
	 * @return True if this activity was actually finished; false in other case
	 */
	public abstract boolean finish(WorkItem wItem);
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}
	
	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, a condition which determines if the 
	 * workgroup can be used or not, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends WorkGroup implements es.ull.isaatc.simulation.core.ActivityWorkGroup, Comparable<ActivityWorkGroup> {
	    /** Workgroup's identifier */
		protected int id;
		/** Priority of the workgroup */
	    protected int priority = 0;
	    /** Availability condition */
	    protected Condition cond;
	    private final String idString; 
		
	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param wg The original workgroup
	     */    
	    protected ActivityWorkGroup(int id, int priority, WorkGroup wg) {
	        this(id, priority, wg, new TrueCondition());
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this workgroup.
	     * @param priority Priority of the workgroup.
	     * @param wg The original workgroup
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, int priority, WorkGroup wg, Condition cond) {
	        super(wg.resourceTypes, wg.needed);
	        this.id = id;
	        this.priority = priority;
	        this.cond = cond;
	        this.idString = new String("(" + Activity.this + ")" + getDescription());
	    }


	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    protected Activity getActivity() {
	        return Activity.this;
	    }
	    
	    /**
	     * Getter for property priority.
	     * @return Value of property priority.
	     */
	    public int getPriority() {
	        return priority;
	    }
	    
	    /**
	     * Checks if there are enough resources to carry out an activity by using this workgroup.   
	     * The "potential" available resources are booked by the element requesting the activity. 
	     * If there are less available resources than needed resources for any resource type, the 
	     * activity can not be carried out, and all the "books" are removed.
	     * Possible conflicts between resources inside the activity are solved by invoking a
	     * branch-and-bound resource distribution algorithm. 
	     * @param wi Work Item trying to carry out the activity with this workgroup 
	     * @return The set of resources which compound the solution. Null if there are not enough
	     * resources to carry out the activity by using this workgroup.
	     */
	    protected ArrayDeque<Resource> isFeasible(WorkItem wi) {

	    	if (!cond.check(wi.getElement()))
	    		return null;

	    	int ned[] = needed.clone();
	    	if (ned.length == 0) // Infinite resources
	    		return new ArrayDeque<Resource>(); 
	        int []pos = {0, -1}; // "Start" position
	        
	        int totalRes = 0;
	        for (int n : ned)
	            totalRes += n;
	        ArrayDeque<Resource> solution = new ArrayDeque<Resource>(totalRes);
	        // B&B algorithm for finding a solution
	        if (findSolution(solution, pos, ned, wi))
	            return solution;
	        return null;
	    }
	    
	    /**
	     * Returns the position [ResourceType, Resource] of the next valid solution. The initial position
	     * <code>pos</code> is supposed as correct.
	     * @param pos Initial position [ResourceType, Resource].
	     * @param nec Resource needed.
	     * @return [ResourceType, Resource] where the next valid solution can be found.
	     */
	    private int []searchNext(int[] pos, int []nec, WorkItem wi) {
	        int []aux = new int[2];
	        aux[0] = pos[0];
	        aux[1] = pos[1];
	        // Searches a resource type that requires resources
	        while (nec[aux[0]] == 0) {
	            aux[0]++;
	            // The second index is reset
	            aux[1] = -1;
	            // No more resources needed ==> SOLUTION
	            if (aux[0] == resourceTypes.length) {
	                return aux;
	            }
	        }
	        // Takes the first resource type
	        ResourceType rt = resourceTypes[aux[0]];
	        // Searches the NEXT available resource
	        aux[1] = rt.getNextAvailableResource(aux[1] + 1, wi);

	        // This resource type don't have enough available resources
	        if (aux[1] == -1)
	            return null;
	        return aux;
	    }

	    /**
	     * Marks a resource as belonging to the solution
	     * @param pos Position [ResourceType, Resource] of the resource
	     */
	    private void mark(int []pos, ArrayDeque<Resource> solution) {
	        Resource res = resourceTypes[pos[0]].getResource(pos[1]);
	        res.setCurrentResourceType(resourceTypes[pos[0]]);
	        solution.push(res);
	    }
	    
	    /**
	     * Removes the mark of a resource as belonging to the solution
	     * @param pos Position [ResourceType, Resource] of the resource
	     */
	    private void unmark(int []pos, ArrayDeque<Resource> solution) {
	        Resource res = resourceTypes[pos[0]].getResource(pos[1]);
	        res.setCurrentResourceType(null);
	        solution.pop();
	    }

	    /**
	     * Makes a depth first search looking for a solution.
	     * @param pos Position to look for a solution [ResourceType, Resource] 
	     * @param ned Resources needed
	     * @return True if a valid solution exists. False in other case.
	     */
	    protected boolean findSolution(ArrayDeque<Resource> solution, int []pos, int []ned, WorkItem wi) {
	        pos = searchNext(pos, ned, wi);
	        // No solution
	        if (pos == null)
	            return false;
	        // No more elements needed => SOLUTION
	        if (pos[0] == resourceTypes.length)
	            return true;
	        // This resource belongs to the solution...
	        mark(pos, solution);
	        ned[pos[0]]--;
	        // ... the search continues
            if (findSolution(solution, pos, ned, wi))
                return true;
	        // There's no solution with this resource. Try without it
	        unmark(pos, solution);
	        ned[pos[0]]++;
	        // ... and the search continues
	        return findSolution(solution, pos, ned, wi);        
	    }
	    
		public int getIdentifier() {
			return id;
		}

		public String getDescription() {
			StringBuilder str = new StringBuilder("WG" + id);
			for (int i = 0; i < resourceTypes.length; i++)
				str.append(" [" + resourceTypes[i] + "," + needed[i] + "]");
			return str.toString();
		}

	    @Override
	    public String toString() {
	    	return idString;
	    }

		public int compareTo(ActivityWorkGroup arg0) {
			if (id < arg0.id)
				return -1;
			if (id > arg0.id)
				return 1;
			return 0;
		}

		@Override
		public Condition getCondition() {
			return cond;
		}

	}

	public long getLastStartTs() {
		return lastStartTs;
	}


	public long getLastFinishTs() {
		return lastFinishTs;
	}

	public void setLastFinishTs(long lastFinishTs) {
		this.lastFinishTs = lastFinishTs;
	}

}
