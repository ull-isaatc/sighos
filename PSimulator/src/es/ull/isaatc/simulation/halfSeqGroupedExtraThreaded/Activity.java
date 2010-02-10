package es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
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
public abstract class Activity extends TimeStampedSimulationObject implements es.ull.isaatc.simulation.common.Activity {
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
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg) {
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
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg, Condition cond) {
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
    public int addWorkGroup(es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg, Condition cond) {    	
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
     * @return True if the activity can be performed. False if the activity isn't feasible.
     */
    protected boolean isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return false;
        Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup wg = iter.next();
            if (wg.isFeasible(wi)) {
                wi.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wi.getExecutionWG());
                return true;
            }            
        }
        stillFeasible = false;
        return false;
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
	public abstract void carryOut(WorkItem wItem);

	/**
	 * Releases the resources required to carry out this activity.
	 * @param wItem Work item which requested this activity
	 * @return True if this activity was actually finished; false in other case
	 */
	public abstract boolean finish(WorkItem wItem);
	
	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, a condition which determines if the 
	 * workgroup can be used or not, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup implements es.ull.isaatc.simulation.common.ActivityWorkGroup, Comparable<ActivityWorkGroup> {
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
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg) {
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
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.halfSeqGroupedExtraThreaded.WorkGroup wg, Condition cond) {
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
	     * @return True if there are more "potential" available resources than needed resources for
	     * this workgroup. False in other case.
	     */
	    protected boolean isFeasible(WorkItem wi) {
	    	boolean conflict = false;
	    	Element elem = wi.getElement();

	    	wi.resetConflictZone();
	    	if (!cond.check(elem))
	    		return false;
	        for (int i = 0; i < resourceTypes.length; i++) {
	            ResourceType rt = resourceTypes[i];       	
	        	int []avail = rt.getAvailable(wi);
	        	// If there are less "potential" available resources than needed
	            if (avail[0] + avail[1] < needed[i]) {
	            	// The element frees the previously booked resources 
	                rt.resetAvailable(wi);
	                i--;
	                for (; i >= 0; i--)
	                    resourceTypes[i].resetAvailable(wi);
	                wi.removeFromConflictZone();
	                return false;            	
	            }
	            // If the available resources WITH conflicts are needed
	            else if (avail[0] < needed[i])
	                conflict = true;
	        }
	        // When this point is reached, that means that the activity is POTENTIALLY feasible
	        wi.waitConflictSemaphore();
	        // Now, this element has exclusive access to its resources. It's time to "recheck"
	        // if the activity is feasible        
	        if (conflict) { // The resource distribution algorithm is invoked
	        	debug("Overlapped resources with " + elem);
	            if (!distributeResources(wi)) {
	                wi.removeFromConflictZone();
	            	wi.signalConflictSemaphore();
	            	return false;
	            }
	        }
	        else if (wi.getConflictZone().size() > 1) {
	        	debug("Possible conflict. Recheck is needed " + elem);
	            int ned[] = needed.clone();
	        	if (!hasSolution(new int[] {0, 0}, ned, wi)) {
	                wi.removeFromConflictZone();
	            	wi.signalConflictSemaphore();
	            	// The element frees the previously booked resources 
	            	for (ResourceType rt : resourceTypes)
	            		rt.resetAvailable(wi);
	        		return false;
	        	}
	        }
	        return true;
	    }
	    
	    /**
	     * Checks if a valid solution can be reached from the current situation. This method 
	     * is used to bound the search tree.
	     * @param pos Initial position.
	     * @param nec Resources needed.
	     * @return True if there is a reachable solution. False in other case.
	     */
	    protected boolean hasSolution(int []pos, int []nec, WorkItem wi) {
	        for (int i = pos[0]; i < resourceTypes.length; i++) {
	            ResourceType rt = resourceTypes[i];
	            int j = pos[1];
	            Resource res;
	            int disp = 0;            
	            while (((res = rt.getResource(j)) != null) && (disp < nec[i])) {
	        		res.waitSemaphore();
	        		// Only resources booked for this SF can be taken into account.
	        		// The resource could have been released after the book phase, so it's needed to recheck this.
	                if (res.isBooked(wi) && (res.getCurrentWI() == null) && (res.getCurrentResourceType() == null))
	                    disp++;
	        		res.signalSemaphore();
	                j++;
	            }
	            if (disp < nec[i])
	                return false;
	        }
	        return true;
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
	    private void mark(int []pos) {
	        Resource res = resourceTypes[pos[0]].getResource(pos[1]);
	        // There's no need to access in mutex this area, because only resources booked by this SF
	        // are taken into account, and only one SF can be at this stage for this resource at the same 
	        // time (due to the conflict zone mutex)
	        res.setCurrentResourceType(resourceTypes[pos[0]]);
	    }
	    
	    /**
	     * Removes the mark of a resource as belonging to the solution
	     * @param pos Position [ResourceType, Resource] of the resource
	     */
	    private void unmark(int []pos) {
	        Resource res = resourceTypes[pos[0]].getResource(pos[1]);
	        // There's no need to access in mutex this area, because only resources booked by this SF
	        // are taken into account, and only one SF can be at this stage for this resource at the same 
	        // time (due to the conflict zone mutex)
	        res.setCurrentResourceType(null);
	    }

	    /**
	     * Makes a depth first search looking for a solution.
	     * @param pos Position to look for a solution [ResourceType, Resource] 
	     * @param ned Resources needed
	     * @return True if a valid solution exists. False in other case.
	     */
	    protected boolean findSolution(int []pos, int []ned, WorkItem wi) {
	        pos = searchNext(pos, ned, wi);
	        // No solution
	        if (pos == null)
	            return false;
	        // No more elements needed => SOLUTION
	        if (pos[0] == resourceTypes.length)
	            return true;
	        // This resource belongs to the solution...
	        mark(pos);
	        ned[pos[0]]--;
	        // Bound
	        if (hasSolution(pos, ned, wi))
	        // ... the search continues
	            if (findSolution(pos, ned, wi))
	                return true;
	        // There's no solution with this resource. Try without it
	        unmark(pos);
	        ned[pos[0]]++;
	        // ... and the search continues
	        return findSolution(pos, ned, wi);        
	    }
	    
	    /**
	     * Distribute the resources when there is a conflict inside the activity.
	     * @param wi Work item trying to carry out the activity with this workgroup 
	     * @return True if a valid solution exists. False in other case.
	     */
	    protected boolean distributeResources(WorkItem wi) {
	        int ned[] = needed.clone();
	        int []pos = {0, -1}; // "Start" position
	        
	        // B&B algorithm for finding a solution
	        if (findSolution(pos, ned, wi))
	            return true;
	        // If there is no solution, the "books" of this element are removed
	        for (ResourceType rt : resourceTypes)
	            rt.resetAvailable(wi);
	        return false;
	    }
	    
	    /**
	     * Catch the resources needed for each resource type to carry out an activity.
	     * @param wi Work item which requires the resources
	     * @return The minimum availability timestamp of the taken resources 
	     */
	    protected long catchResources(WorkItem wi) {
	    	long minAvailability = Long.MAX_VALUE;
	    	for (int i = 0; i < resourceTypes.length; i++)
	    		minAvailability = Math.min(minAvailability, resourceTypes[i].catchResources(needed[i], wi));
	    	// When this point is reached, that means that the resources have been completely taken
	    	wi.signalConflictSemaphore();
	    	return minAvailability;
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
