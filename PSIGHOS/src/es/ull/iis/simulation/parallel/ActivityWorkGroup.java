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
     * Checks if there are enough {@link Resource}s to carry out this activity by using this workgroup.   
     * The "potential" available {@link Resource}s are booked by the {@link ElementEngine} requesting this 
     * activity. If there are less <b>available</b> resources than <b>needed</b> resources for any 
     * {@link ResourceType}, this activity can not be carried out, and all the "books" are removed.
     * Possible conflicts between resources inside the activity are solved by invoking a
     * branch-and-bound resource distribution algorithm. 
     * @param wi {@link WorkItem} trying to carry out this activity with this workgroup 
     * @return <tt>True</tt> if there are more "potential" available resources than needed resources for
     * this workgroup. <tt>False</tt> otherwise.
     */
    protected boolean isFeasible(WorkItem wi) {
    	final ElementEngine elem = wi.getElement();

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
    
    @Override
	public int compareTo(ActivityWorkGroup arg0) {
		if (id < arg0.id)
			return -1;
		if (id > arg0.id)
			return 1;
		return 0;
	}
}