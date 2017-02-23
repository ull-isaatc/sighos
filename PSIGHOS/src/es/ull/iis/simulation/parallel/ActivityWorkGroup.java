package es.ull.iis.simulation.parallel;

import java.util.ArrayDeque;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;

/**
 * A set of resources needed for carrying out this activity. A workgroup (WG) consists 
 * on a set of &lt{@link ResourceType}, {@link Integer}&gt pairs, a {@link Condition} 
 * which determines if the WG can be used or not, and the priority of the WG inside this 
 * activity.
 * @author Iván Castilla Rodríguez
 */
public class ActivityWorkGroup extends es.ull.iis.simulation.parallel.WorkGroup implements es.ull.iis.simulation.model.ActivityWorkGroupEngine, Comparable<ActivityWorkGroup> {
    /**
	 * 
	 */
	private final Activity activity;
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
     * @param activity TODO
     */    
    protected ActivityWorkGroup(Activity activity, int id, int priority, es.ull.iis.simulation.parallel.WorkGroup wg) {
        this(activity, id, priority, wg, new TrueCondition());
    }
    
    /**
     * Creates a new instance of WG which contains the same resource types
     * than an already existing one.
     * @param id Identifier of this WG.
     * @param priority Priority of the WG.
     * @param wg The original WG
     * @param cond Availability condition
     * @param activity TODO
     */    
    protected ActivityWorkGroup(Activity activity, int id, int priority, es.ull.iis.simulation.parallel.WorkGroup wg, Condition cond) {
        super(wg.resourceTypes, wg.needed);
		this.activity = activity;
        this.id = id;
        this.priority = priority;
        this.cond = cond;
        this.idString = new String("(" + this.activity + ")" + getDescription());
    }


    /**
     * Returns the activity this WG belongs to.
     * @return Activity this WG belongs to.
     */    
    protected Activity getActivity() {
        return this.activity;
    }
    
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
    	final Element elem = wi.getElement();

    	wi.resetConflictZone();
    	if (!cond.check(elem))
    		return false;
    	
    	if (needed.length == 0) { // Infinite resources
    		wi.waitConflictSemaphore(); // FIXME: unneeded, but fails if removed
    		return true;
    	}
    	
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
	        	this.activity.debug("Possible conflict. Recheck is needed " + elem);
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
    	// Checks the current RT
        if (!resourceTypes[pos[0]].checkNeeded(pos[1], nec[pos[0]]))
        	return false;
        // For the next resource types, the first index must be 0
        for (int i = pos[0] + 1; i < resourceTypes.length; i++) {
            if (!resourceTypes[i].checkNeeded(0, nec[i]))
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