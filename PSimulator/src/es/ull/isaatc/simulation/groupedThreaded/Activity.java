package es.ull.isaatc.simulation.groupedThreaded;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;
import es.ull.isaatc.util.PrioritizedTable;

/**
 * A task which could be carried out by a {@link WorkItem} and requires certain amount and 
 * type of {@link Resource resources} to be performed.  An activity is characterized by its 
 * priority and a set of {@link WorkGroup Workgroups} (WGs). Each WG represents a combination 
 * of {@link ResourceType resource types} required to carry out the activity.<p>
 * Each activity is attached to an {@link ActivityManager}, which manages the access to the activity.<p>
 * An activity is potentially feasible if there is no proof that there are not enough resources
 * to perform it. An activity is feasible if it's potentially feasible and there is at least one
 * WG with enough available resources to perform the activity. The WGs are checked in 
 * order according to some priorities, and can also have an associated condition which must be 
 * accomplished to be selected.<p>
 * An activity can be requested (that is, check if the activity is feasible) by a valid 
 * {@link WorkItem}. 
 * If the activity is not feasible, the work item is added to a queue until new resources are 
 * available. If the activity is feasible, the work item "carries out" the activity, that is, 
 * catches the resources needed to perform the activity. Whenever it is determined that the 
 * activity has finished, the work item releases the resources previously caught.<p>
 * An activity can also define cancellation periods for each one of the resource types it uses. 
 * If a work item takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * @author Carlos Martín Galán
 */
public abstract class Activity extends TimeStampedSimulationObject implements es.ull.isaatc.simulation.common.Activity {
    /** Priority. 0 for the higher priority, higher values for lower priorities */
    protected int priority = 0;
    /** A brief description of this activity */
    protected final String description;
    /** Total amount of {@link WorkItem WorkItems} waiting for carrying out this activity */
    protected int queueSize = 0;
    /** The activity manager this activity is attached to */
    protected ActivityManager manager = null;
    /** WGs available to perform this activity */
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Resource cancellation table */
    protected final ArrayList<CancelListEntry> cancellationList;
    /** Timestamp corresponding to the last time this activity was started */
    protected long lastStartTs = 0;
    /** Timestamp corresponding to the last time this activity was finished */
    protected long lastFinishTs = 0;

	/**
     * Creates a new activity with the highest priority.
     * @param id Activity's identifier
     * @param simul The {@link Simulation} where this activity is used
     * @param description A short text describing this activity
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier
     * @param simul The {@link Simulation} where this activity is used
     * @param description A short text describing this activity
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

    @Override
	public String getDescription() {
		return description;
	}

	/**
     * Returns the priority of this activity.
     * @return Priority of this activity
     */
    @Override
    public int getPriority() {
        return priority;
    }
    
    /**
     * Returns the {@link ActivityManager} where this activity is located.
     * @return The {@link ActivityManager} where this activity is located
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Sets the {@link ActivityManager} where this activity is located. Also
     * adds this activity to the manager.
     * @param manager {@link ActivityManager} where this activity is located.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
    /**
     * Returns <tt>true</tt> if this activity is interruptible, i.e., the activity is
     * suspended when any of the the resources taken to perform the activity finalize 
     * their availability. The activity can be resumed when there are available resources 
     * again (<b>but not necessarily the same resources</b>). 
     * <p>By default, an activity is not interruptible.  
     * @return Always <tt>false</tt>. Subclasses overriding this method must change the 
     * default behavior. 
     */
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
     * Creates a new WG for this activity, with the specified priority and using the resource
     * types indicated by <code>wg</code>.
     * @param priority Priority of the WG
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, priority, wg));
        return wgId;
    }
    
    /**
     * Creates a new WG for this activity, with the specified priority and using the resource
     * types indicated by <code>wg</code>. This WG is only available if <code>cond</code> is 
     * <code>true</code>.
     * @param priority Priority of the WG
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @param cond Availability condition
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg, Condition cond) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new WG for this activity with the highest level of priority and using the 
     * resource types indicated by <code>wg</code>.
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg) {    	
        return addWorkGroup(0, wg);
    }
    
    /**
     * Creates a new WG for this activity with the highest level of priority and using the 
     * resource types indicated by <code>wg</code>. This WG is only available if 
     * <code>cond</code> is <code>true</code>.
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @param cond Availability condition
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(0, wg, cond);
    }

    /**
     * Returns an iterator over the WGs of this activity.
     * @return An iterator over the WGs that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

    @Override
    public ActivityWorkGroup getWorkGroup(int wgId) {
        final Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	final ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
    
	/**
     * Checks if this activity can be carried out with any of its WGs. Firstly checks if 
     * the activity is not potentially feasible, then goes through the WGs looking for an 
     * appropriate one. If this activity can't be performed with any of the WGs it's marked 
     * as not potentially feasible. 
     * @param wi Work Item wanting to carry out this activity 
     * @return <code>True</code> if this activity can be carried out with any one of its 
     * WGs. <code>False</code> in other case.
     */
    protected boolean isFeasible(WorkItem wi) {
    	if (!stillFeasible)
    		return false;
        final Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	final ActivityWorkGroup wg = iter.next();
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
     * Sets this activity as potentially feasible.
     */
    protected void resetFeasible() {
    	stillFeasible = true;
    }
    
    /**
     * Adds a work item to the queue.
     * @param wi Work Item added
     */
    protected void queueAdd(WorkItem wi) {
        manager.queueAdd(wi);
    	queueSize++;
		wi.getElement().incInQueue(wi);
		wi.getFlow().inqueue(wi.getElement());
    }
    
    /**
     * Removes a specific work item from the queue.
     * @param wi Work Item that must be removed from the queue.
     */
    protected void queueRemove(WorkItem wi) {
    	manager.queueRemove(wi);
    	queueSize--;
		wi.getElement().decInQueue(wi);
    }

    /**
     * Returns how many work items are waiting to carry out this activity. 
     * @return The size of this activity's queue
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
	 * Adds a new {@link ResourceType} to the cancellation list.
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
	
	@Override
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}
	
	/**
	 * A {@link WorkGroup} which implements {@link es.ull.isaatc.simulation.common.ActivityWorkGroup ActivityWorkGroup}.
	 * It may include a {@link Condition} which determines if the workgroup can be used or not to perform the 
	 * activity. It also defines the priority of the workgroup with respect to the rest of workgroups of this
	 * activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends es.ull.isaatc.simulation.groupedThreaded.WorkGroup implements es.ull.isaatc.simulation.common.ActivityWorkGroup, Comparable<ActivityWorkGroup> {
	    /** Workgroup's identifier */
		protected final int id;
		/** Priority of this WG */
	    protected final int priority;
	    /** Availability condition */
	    protected final Condition cond;
	    /** Precomputed string which identifies this WG */
	    private final String idString; 

	    /**
	     * Creates a new instance of WorkGroup which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this WG.
	     * @param priority Priority of the WG.
	     * @param wg The original WG
	     */    
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg) {
	        this(id, priority, wg, new TrueCondition());
	    }
	    
	    /**
	     * Creates a new instance of WG which contains the same resource types
	     * than an already existing one.
	     * @param id Identifier of this WG.
	     * @param priority Priority of the WG.
	     * @param wg The original WG
	     * @param cond Availability condition
	     */    
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.groupedThreaded.WorkGroup wg, Condition cond) {
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
	     * Returns the priority of this workgroup with respect to the rest of workgroups defined
	     * in this activity.
	     * @return Relative priority of this workgroup
	     */
	    @Override
	    public int getPriority() {
	        return priority;
	    }
	    
	    /**
	     * Checks if there are enough {@link Resource}s to carry out this activity by using this workgroup.   
	     * The "potential" available {@link Resource}s are booked by the {@link Element} requesting this 
	     * activity. If there are less <b>available</b> resources than <b>needed</b> resources for any 
	     * {@link ResourceType}, this activity can not be carried out, and all the "books" are removed.
	     * Possible conflicts between resources inside the activity are solved by invoking a
	     * branch-and-bound resource distribution algorithm. 
	     * @param wi {@link WorkItem} trying to carry out this activity with this workgroup 
	     * @return <tt>True</tt> if there are more "potential" available resources than needed resources for
	     * this workgroup. <tt>False</tt> otherwise.
	     */
	    protected boolean isFeasible(WorkItem wi) {
	    	boolean conflict = false;
	    	final Element elem = wi.getElement();

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
	    	// Start revision in current position
            int j = pos[1];
	        for (int i = pos[0]; i < resourceTypes.length; i++) {
	            ResourceType rt = resourceTypes[i];
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
	            // For the following RTs, starts from the first one
	            j = 0;
	            if (disp < nec[i])
	                return false;
	        }
	        return true;
	    }
	    
	    /**
	     * Returns the position [{@link ResourceType}, {@link Resource}] of the next valid 
	     * solution. The initial position <code>pos</code> is supposed to be correct.
	     * @param pos Initial position [ResourceType, Resource].
	     * @param nec Resources needed.
	     * @return [ResourceType, Resource] where the next valid solution can be found; or
	     * <code>null</code> if no solution was found. 
	     */
	    private int []searchNext(int[] pos, int []nec, WorkItem wi) {
	        final int []aux = new int[2];
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
	     * Catch the resources needed for each resource type to carry out this activity.
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

	    @Override
		public int getIdentifier() {
			return id;
		}

	    @Override
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

	    @Override
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
