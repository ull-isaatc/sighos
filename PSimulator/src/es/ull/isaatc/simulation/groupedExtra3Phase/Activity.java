package es.ull.isaatc.simulation.groupedExtra3Phase;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.TreeMap;

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
    protected final PrioritizedTable<ActivityWorkGroup> workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;
    /** Resource cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList = new TreeMap<ResourceType, Long>();
    /** Last activity start */
    protected long lastStartTs = 0;
    /** Last activity finish */
    protected long lastFinishTs = 0;

	/**
     * Creates a new activity with the highest priority.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this activity.
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
        simul.add(this);
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
     * Returns the activity manager this activity is attached to.
     * @return The activity manager this activity is attached to
     */
    public ActivityManager getManager() {
        return manager;
    }

    /**
     * Sets the activity manager this activity is attached to. It also
     * adds this activity to the manager.
     * @param manager The activity manager.
     */
    public void setManager(ActivityManager manager) {
        this.manager = manager;
        manager.add(this);
    }
    
    /**
     * Creates a new WG for this activity, with the specified priority and using the resource
     * types indicated by <code>wg</code>.
     * @param priority Priority of the WG
     * @param wg The set of pairs <ResurceType, amount> which can be used to carry out this activity
     * @return The identifier of the new WG.
     */
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg) {
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
    public int addWorkGroup(int priority, es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg, Condition cond) {
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
    public int addWorkGroup(es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg) {    	
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
    public int addWorkGroup(es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg, Condition cond) {    	
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
    	// WGs with the same priority are traversed in random order
        final Iterator<ActivityWorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	final ActivityWorkGroup wg = iter.next();
            if (wg.isFeasible(wi)) {
                wi.setExecutionWG(wg);
        		debug("Can be carried out by\t" + wi.getElement().getIdentifier() + "\t" + wi.getExecutionWG());
                return true;
            }            
        }
        // No valid WG was found
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
    protected synchronized void queueAdd(WorkItem wi) {
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
		cancellationList.put(rt, duration);
	}
	
	/**
	 * Returns the duration of the cancellation of a resource with the specified
	 * resource type.
	 * @param rt Resource Type
	 * @return The duration of the cancellation
	 */
	public long getResourceCancelation(ResourceType rt) {
		final Long dur = cancellationList.get(rt);
		if (dur == null)
			return 0;
		return dur;
	}
	
	/**
	 * Returns true if this activity is the main activity that an element can do.
	 * @return True if the activity requires an element MUTEX.
	 */
	public abstract boolean mainElementActivity();
	
	/**
	 * Requests this activity. Checks if this activity is feasible by the
	 * specified work item. If the activity is feasible, {@link #carryOut(WorkItem)}
	 * is invoked; in other case, the work item is added to this activity's queue.
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
	 * A set of resources needed for carrying out this activity. A workgroup (WG) consists 
	 * on a set of &lt{@link ResourceType}, {@link Integer}&gt pairs, a {@link Condition} 
	 * which determines if the WG can be used or not, and the priority of the WG inside this 
	 * activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup implements es.ull.isaatc.simulation.common.ActivityWorkGroup, Comparable<ActivityWorkGroup> {
	    /** The identifier of this WG */
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
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg) {
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
	    protected ActivityWorkGroup(int id, int priority, es.ull.isaatc.simulation.groupedExtra3Phase.WorkGroup wg, Condition cond) {
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
	    
	    @Override
	    public int getPriority() {
	        return priority;
	    }
	    
	    /**
	     * Checks if there are enough resources to carry out this activity by using this WG.   
	     * The "potential" available resources are booked by the work item requesting the activity. 
	     * If there are less available resources than needed resources for any resource type, the 
	     * activity can not be carried out, and all the "books" are removed.
	     * In order to avoid possible conflicts between resources which appear in more than one 
	     * resource type in the activity, a branch-and-bound resource distribution algorithm is 
	     * invoked. 
	     * @param wi Work Item trying to carry out the activity with this WG 
	     * @return True if there are more "potential" available resources than needed resources for
	     * this WG. False in other case.
	     */
	    protected boolean isFeasible(WorkItem wi) {
	    	final Element elem = wi.getElement();

	    	wi.resetConflictZone();
	    	if (!cond.check(elem))
	    		return false;
	    	
	        int ned[] = needed.clone();
	        int []pos = {0, -1}; // "Start" position
	        
	        // B&B algorithm to find a solution
	        while (findSolution(pos, ned, wi)) {
        		wi.waitConflictSemaphore();
        		// All the resources taken for the solution only appears in this AM 
	        	if (!wi.isConflictive()) 
		            return true;
	        	// Any one of the resources taken for the solution also appears in a different AM 
	        	else {
		        	debug("Possible conflict. Recheck is needed " + elem);
	        		// A recheck is needed
	        		if (wi.checkCaughtResources()) {
	        			return true;
	        		}
	        		else {
	        			// Resets the solution
	        			wi.signalConflictSemaphore();
	        			final ArrayDeque<Resource> oldSolution = wi.getCaughtResources(); 
	        			while (!oldSolution.isEmpty()) {
	        				Resource res = oldSolution.peek();
	        				res.removeFromSolution(wi);
	        			}
	        			ned = needed.clone();
	        			pos[0] = 0;
	        			pos[1] = -1;
	        		}
	        	}
	        }
	        // This point is reached only if no solution was found
	        wi.removeFromConflictZone();
	        return false;
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
	            if (!resourceTypes[i].checkNeeded(pos[1], nec[i]))
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
	        // Takes the first resource type and searches the NEXT available resource
	        aux[1] = resourceTypes[aux[0]].getNextAvailableResource(aux[1] + 1, wi);
	        // This resource type don't have enough available resources
	        if (aux[1] == -1)
	        	return null;

	        return aux;
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
	        ned[pos[0]]--;
	        // Bound
	        if (hasSolution(pos, ned, wi))
	        // ... the search continues
	            if (findSolution(pos, ned, wi))
	                return true;
	        // There's no solution with this resource. Try without it
	        final Resource res = resourceTypes[pos[0]].getResource(pos[1]);
	        res.removeFromSolution(wi);
	        ned[pos[0]]++;
	        // ... and the search continues
	        return findSolution(pos, ned, wi);        
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
