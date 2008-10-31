package es.ull.isaatc.simulation;

import java.util.EnumSet;
import java.util.Iterator;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.RecoverableState;
import es.ull.isaatc.util.*;

/**
 * A task which could be carry out by an element. An activity is characterized by its priority,
 * presentiality, and a set of workgropus. Each workgroup represents a combination of resource 
 * types required for carrying out the activity, and the duration of the activity when performed
 * with this workgroup.<p>
 * A normal activity is presential, that is, an element carrying out this activity can't make 
 * anything else; and ininterruptible, i.e., once started, the activity keeps its resources until
 * it's finished, even if the resources become unavailable while the activity is being performed.
 * This two characteristics are customizable by means of the <code>Modifier</code> enum type. An 
 * activity can be <code>NONPRESENTIAL</code>, when the element can perform other activities while
 * it's performing this one; and <code>INTERRUPTIBLE</code>, when the activity can be interrupted, 
 * and later continued, if the resources become unavailable while the activity is being performed.
 * @author Carlos Martín Galán
 */
public class Activity extends TimeStampedSimulationObject implements Prioritizable, RecoverableState<ActivityState>, Describable {
	/** Indicates special characteristics of this activity */
	public enum Modifier {
	    /** Indicates that this activity is non presential, i.e., an element can perform other activities at
	     * the same time */
		NONPRESENTIAL,
		/** Indicates that the activity can be interrupted in case the required resources end their
		 * availability time */
		INTERRUPTIBLE
	}
	/** The set of modifiers of this activity. */
    protected EnumSet<Modifier> modifiers;
    /** Priority of the activity */
    protected int priority = 0;
    /** A brief description of the activity */
    protected String description;
    /** Total of single flows that are waiting for this activity */
    protected int queueSize = 0;
    /** Activity manager which this activity belongs to */
    protected ActivityManager manager = null;
    /** Work Group Pool */
    protected PrioritizedTable<WorkGroup> workGroupTable;
    /** Indicates that the activity is potentially feasible. */
    protected boolean stillFeasible = true;

    /**
     * Creates a new activity.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public Activity(int id, Simulation simul, String description) {
        this(id, simul, description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public Activity(int id, Simulation simul, String description, int priority) {
        this(id, simul, description, priority, EnumSet.noneOf(Modifier.class));
    }
    
    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public Activity(int id, Simulation simul, String description, EnumSet<Modifier> modifiers) {
        this(id, simul, description, 0, modifiers);
    }

    /**
     * Creates a new activity.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public Activity(int id, Simulation simul, String description, int priority, EnumSet<Modifier> modifiers) {
        super(id, simul);
        this.modifiers = modifiers;
        this.description = description;
        this.priority = priority;
        workGroupTable = new PrioritizedTable<WorkGroup>();
        simul.add(this);
    }

    /**
     * Indicates if the activity requires the presence of the element in order to be carried out. 
     * @return The "presenciality" of the activity.
     */
    public boolean isNonPresential() {
        return modifiers.contains(Modifier.NONPRESENTIAL);
    }
    
    /**
     * Indicates if the activity can be interrupted in case the required resources end their
     * availability time.
	 * @return True if the activity can be interrupted. False if it keeps the resources even 
	 * if they become not available.
	 */
	public boolean isInterruptible() {
		return modifiers.contains(Modifier.INTERRUPTIBLE);
	}

	/**
     * Returns a string describing the activity 
	 * @return a brief description of the activity
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
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new WorkGroup(wgId, duration, priority, wg));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. The workgroup 
     * is added and returned in order to be used.
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, es.ull.isaatc.simulation.WorkGroup wg) {    	
        return addWorkGroup(duration, 0, wg);
    }

    /**
     * Creates a new workgroup for this activity. The workgroup is added and returned in order
     * to be used.
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, int priority) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new WorkGroup(wgId, duration, priority));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. The workgroup 
     * is added and returned in order to be used.
     * @param duration Duration of the activity when performed with the new workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration) {    	
        return addWorkGroup(duration, 0);
    }

    /**
     * Returns an iterator over the workgroups of this activity.
     * @return An iterator over the workgroups that can perform this activity.
     */
    public Iterator<WorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public WorkGroup getWorkGroup(int wgId) {
        Iterator<WorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	WorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
    
    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @param rt Resource Type
     * @param needed Needed units
     * @return True if the specified id corresponds to an existent WG; false in other case.
     */
    public boolean addWorkGroupEntry(int wgId, ResourceType rt, int needed ) {
    	WorkGroup wg = getWorkGroup(wgId);
    	if (wg != null) {
    		wg.add(rt, needed);
    		return true;
    	}
        return false;
    }
    
	/**
     * Checks if this activity can be performed with any of its workgroups. Firstly 
     * checks if the activity is not potentially feasible, then goes through the 
     * workgroups looking for an appropriate one. If the activity can't be performed with 
     * any of the workgroups it's marked as not potentially feasible. 
     * @param sf Single flow which contains the activity 
     * @return True if the activity can be performed. False if the activity isn't feasible.
     */
    protected boolean isFeasible(SingleFlow sf) {
    	if (!stillFeasible)
    		return false;
        Iterator<WorkGroup> iter = workGroupTable.randomIterator();
        while (iter.hasNext()) {
        	WorkGroup wg = iter.next();
            if (wg.isFeasible(sf)) {
                sf.setExecutionWG(wg);
                if (!isNonPresential())
                	sf.getElement().setCurrentSF(sf);
				debug("Can be carried out by\t" + sf.getElement().getIdentifier() + "\t" + wg);
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
     * Add a single flow to the element queue.
     * @param sf Single Flow added
     */
    protected void queueAdd(SingleFlow sf) {
        manager.queueAdd(sf);
    	queueSize++;
    }
    
    /**
     * Remove a specific single flow from the element queue.
     * @param sf Single flow that must be removed from the element queue.
     */
    protected void queueRemove(SingleFlow sf) {
    	manager.queueRemove(sf);
    	queueSize--;
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
	public double getTs() {
		return manager.getTs();
	}

	/**
	 * Gets the state of this activity. The state of an activity consists on the queue of single flows.
	 * @return This activity's state
	 */
	public ActivityState getState() {
		ActivityState state = new ActivityState(id);
		return state;
	}

	/**
	 * Sets the state of this activity. The state of an activity consists on the queue of single flows.
	 * @param state The activity's state.
	 */
	public void setState(ActivityState state) {
	}

	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
	 * this workgroup, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class WorkGroup extends es.ull.isaatc.simulation.WorkGroup implements Prioritizable {
	    /** Duration of the activity when using this WG */
	    protected TimeFunction duration;
	    /** Priority of the workgroup */
	    protected int priority = 0;
		
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     */    
	    protected WorkGroup(int id, SimulationTimeFunction duration, int priority) {
	        super(id, Activity.this.simul, "WG" + id + "-ACT" + Activity.this.id);
	        this.duration = duration.getFunction();
	        this.priority = priority;
	    }

	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     */    
	    protected WorkGroup(int id, SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
	        super(id, wg.simul, wg.description);
	        this.duration = duration.getFunction();
	        this.priority = priority;
	        this.resourceTypeTable.addAll(wg.resourceTypeTable);
	    }

	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    protected Activity getActivity() {
	        return Activity.this;
	    }

	    /**
	     * Returns the duration of the activity where this workgroup is used. 
	     * The value returned by the random number function could be negative. 
	     * In this case, it returns 0.0.
	     * @return The activity duration.
	     */
	    public double getDuration() {
	        return duration.getPositiveValue(getTs());
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
	     * @param sf Single flow trying to carry out the activity with this workgroup 
	     * @return True if there are more "potential" available resources than needed resources for
	     * thiw workgroup. False in other case.
	     */
	    protected boolean isFeasible(SingleFlow sf) {
	    	boolean conflict = false;

	    	sf.resetConflictZone();
	        for (int i = 0; i < resourceTypeTable.size(); i++) {
	            ResourceTypeTableEntry rttEntry = resourceTypeTable.get(i);       	
	        	ResourceType rt = rttEntry.getResourceType();
	        	int []avail = rt.getAvailable(sf);
	        	// If there are less "potential" available resources than needed
	            if (avail[0] + avail[1] < rttEntry.getNeeded()) {
	            	// The element frees the previously booked resources 
	                rt.resetAvailable(sf);
	                i--;
	                for (; i >= 0; i--)
	                    resourceTypeTable.get(i).getResourceType().resetAvailable(sf);
	                sf.removeFromConflictZone();
	                return false;            	
	            }
	            // If the available resources WITH conflicts are needed
	            else if (avail[0] < rttEntry.getNeeded())
	                conflict = true;
	        }
	        // When this point is reached, that means that the activity is POTENTIALLY feasible
	        sf.waitConflictSemaphore();
	        // Now, this element has exclusive access to its resources. It's time to "recheck"
	        // if the activity is feasible        
	        if (conflict) { // The resource distribution algorithm is invoked
	        	debug("Overlapped resources with " + sf.getElement());
	            if (!distributeResources(sf)) {
	                sf.removeFromConflictZone();
	            	sf.signalConflictSemaphore();
	            	return false;
	            }
	        }
	        else if (sf.getConflictZone().size() > 1) {
	        	debug("Possible conflict. Recheck is needed " + sf.getElement());
	            int ned[] = new int[resourceTypeTable.size()];
	            int []pos = {0, 0}; // "Start" position
	            for (int i = 0; i < resourceTypeTable.size(); i++)
	                ned[i] = resourceTypeTable.get(i).getNeeded();
	        	if (!hasSolution(pos, ned)) {
	                sf.removeFromConflictZone();
	            	sf.signalConflictSemaphore();
	            	// The element frees the previously booked resources 
	            	for (ResourceTypeTableEntry rttEntry : resourceTypeTable)
	            		rttEntry.getResourceType().resetAvailable(sf);
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
	    protected boolean hasSolution(int []pos, int []nec) {
	        for (int i = pos[0]; i < resourceTypeTable.size(); i++) {
	            ResourceTypeTableEntry actual = resourceTypeTable.get(i);
	            int j = pos[1];
	            Resource res;
	            int disp = 0;            
	            while (((res = actual.getResource(j)) != null) && (disp < nec[i])) {
	                // FIXME Debería bastar con preguntar por el RT
	                if ((res.getCurrentSF() == null) && (res.getCurrentResourceType() == null))
	                    disp++;
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
	    private int []searchNext(int[] pos, int []nec, SingleFlow sf) {
	        int []aux = new int[2];
	        aux[0] = pos[0];
	        aux[1] = pos[1];
	        // Searches a resource type that requires resources
	        while (nec[aux[0]] == 0) {
	            aux[0]++;
	            // The second index is reset
	            aux[1] = -1;
	            // No more resources needed ==> SOLUTION
	            if (aux[0] == resourceTypeTable.size()) {
	                return aux;
	            }
	        }
	        // Takes the first resource type
	        ResourceType rt = resourceTypeTable.get(aux[0]).getResourceType();
	        // Searches the NEXT available resource
	        aux[1] = rt.getNextAvailableResource(aux[1] + 1, sf);

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
	        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
	        res.setCurrentResourceType(resourceTypeTable.get(pos[0]).getResourceType());
	    }
	    
	    /**
	     * Removes the mark of a resource as belonging to the solution
	     * @param pos Position [ResourceType, Resource] of the resource
	     */
	    private void unmark(int []pos) {
	        Resource res = resourceTypeTable.get(pos[0]).getResource(pos[1]);
	        res.setCurrentResourceType(null);
	    }

	    /**
	     * Makes a depth first search looking for a solution.
	     * @param pos Position to look for a solution [ResourceType, Resource] 
	     * @param ned Resources needed
	     * @return True if a valid solution exists. False in other case.
	     */
	    protected boolean findSolution(int []pos, int []ned, SingleFlow sf) {
	        pos = searchNext(pos, ned, sf);
	        // No solution
	        if (pos == null)
	            return false;
	        // No more elements needed => SOLUTION
	        if (pos[0] == resourceTypeTable.size())
	            return true;
	        // This resource belongs to the solution...
	        mark(pos);
	        ned[pos[0]]--;
	        // Bound
	        if (hasSolution(pos, ned))
	        // ... the search continues
	            if (findSolution(pos, ned, sf))
	                return true;
	        // There's no solution with this resource. Try without it
	        unmark(pos);
	        ned[pos[0]]++;
	        // ... and the search continues
	        return findSolution(pos, ned, sf);        
	    }
	    
	    /**
	     * Distribute the resources when there is a conflict inside the activity.
	     * @param sf Single flow trying to carry out the activity with this workgroup 
	     * @return True if a valid solution exists. False in other case.
	     */
	    protected boolean distributeResources(SingleFlow sf) {
	        int ned[] = new int[resourceTypeTable.size()];
	        int []pos = {0, -1}; // "Start" position
	        
	        for (int i = 0; i < resourceTypeTable.size(); i++)
	            ned[i] = resourceTypeTable.get(i).getNeeded();
	        // B&B algorithm for finding a solution
	        if (findSolution(pos, ned, sf))
	            return true;
	        // If there is no solution, the "books" of this element are removed
	        for (int i = 0; i < resourceTypeTable.size(); i++)
	            resourceTypeTable.get(i).getResourceType().resetAvailable(sf);
	        return false;
	    }
	    
	    /**
	     * Catch the resources needed for each resource type to carry out an activity.
	     * @param sf Single flow which requires the resources
	     * @return The minimum availability timestamp of the taken resources 
	     */
	    protected double catchResources(SingleFlow sf) {
	    	double minAvailability = Double.MAX_VALUE;
	    	for (ResourceTypeTableEntry rtte : resourceTypeTable) {
	    		minAvailability = Math.min(minAvailability, rtte.getResourceType().catchResources(rtte.getNeeded(), sf));
	    	}
	    	// When this point is reached, that means that the resources have been completely taken
	    	sf.signalConflictSemaphore();
	    	return minAvailability;
	    }

	    @Override
	    public String toString() {
	    	return new String("(" + Activity.this + ")" + super.toString());
	    }

	}
}
