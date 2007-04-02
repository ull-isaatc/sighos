package es.ull.isaatc.simulation;

import java.util.Iterator;

import es.ull.isaatc.function.TimeFunction;
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
public class Activity extends TimeStampedSimulationObject implements Prioritizable, RecoverableState<ActivityState>, Describable {
    /** Priority of the activity */
    protected int priority = 0;
    /** A brief description of the activity */
    protected String description;
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
        super(id, simul);
        this.description = description;
        this.priority = priority;
        this.presential = presential;
        queue = new RemovablePrioritizedTable<SingleFlow>();
        workGroupTable = new NonRemovablePrioritizedTable<WorkGroup>();
        simul.add(this);
    }

    /**
     * Indicates if the activity requires the presence of the element in order to be carried out. 
     * @return The "presenciality" of the activity.
     */
    public boolean isPresential() {
        return presential;
    }
    
    /**
	 * @return the description
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
    public int addWorkGroup(TimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
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
    public int addWorkGroup(TimeFunction duration, es.ull.isaatc.simulation.WorkGroup wg) {    	
        return addWorkGroup(duration, 0, wg);
    }

    /**
     * Creates a new workgroup for this activity. The workgroup is added and returned in order
     * to be used.
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(TimeFunction duration, int priority) {
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
    public int addWorkGroup(TimeFunction duration) {    	
        return addWorkGroup(duration, 0);
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
    protected WorkGroup getWorkGroup(int wgId) {
        Iterator<WorkGroup> iter = workGroupTable.iterator(NonRemovablePrioritizedTable.IteratorType.FIFO);
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
     * Checks if this activity can be performed with any of its workgroups.
     * @param sf Single flow which contains the activity 
     * @return True if the activity can be performed. False if the activity isn't feasible.
     */
    protected boolean isFeasible(SingleFlow sf) {
        Iterator<WorkGroup> iter = workGroupTable.iterator(NonRemovablePrioritizedTable.IteratorType.RANDOM);
        while (iter.hasNext()) {
        	WorkGroup wg = iter.next();
            if (wg.isFeasible(sf)) {
                sf.setExecutionWG(wg);
                if (isPresential())
                	sf.getElement().setCurrentSF(sf);
                return true;
            }            
        }
        return false;
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

	/**
* A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
	 * this workgroup, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	class WorkGroup extends es.ull.isaatc.simulation.WorkGroup implements Prioritizable {
	    /** Duration of the activity when using this WG */
	    protected TimeFunction duration;
	    /** Priority of the workgroup */
	    protected int priority = 0;
		
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duracion Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     */    
	    protected WorkGroup(int id, TimeFunction duration, int priority) {
	        super(id, Activity.this.simul, "WG" + id + "-ACT" + Activity.this.id);
	        this.duration = duration;
	        this.priority = priority;
	    }

	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duracion Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     */    
	    protected WorkGroup(int id, TimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
	        super(id, wg.simul, wg.description);
	        this.duration = duration;
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
	     * Returns the duration of the activity when this workgroup is used. 
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
	    private int []searchNext(int[] pos, int []nec) {
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
	        aux[1] = rt.getNextAvailableResource(aux[1] + 1);

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
	    protected boolean findSolution(int []pos, int []ned) {
	        pos = searchNext(pos, ned);
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
	            if (findSolution(pos, ned))
	                return true;
	        // There's no solution with this resource. Try without it
	        unmark(pos);
	        ned[pos[0]]++;
	        // ... and the search continues
	        return findSolution(pos, ned);        
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
	        if (findSolution(pos, ned))
	            return true;
	        // If there is no solution, the "books" of this element are removed
	        for (int i = 0; i < resourceTypeTable.size(); i++)
	            resourceTypeTable.get(i).getResourceType().resetAvailable(sf);
	        return false;
	    }
	    
	    /**
	     * Catch the resources needed for each resource type to carry out an activity.
	     * @param sf Single flow which requires the resources
	     */
	    protected void catchResources(SingleFlow sf) {
	       for (ResourceTypeTableEntry rtte : resourceTypeTable)
	           rtte.getResourceType().catchResources(rtte.getNeeded(), sf);
	       // When this point is reached, that means that the resources have been completely taken
	       sf.signalConflictSemaphore();
	    }

	    @Override
	    public String toString() {
	    	return new String("(" + Activity.this + ")" + super.toString());
	    }

	}
}
