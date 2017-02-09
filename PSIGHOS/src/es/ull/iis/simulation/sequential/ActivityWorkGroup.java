package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.sequential.flow.RequestResourcesFlow;

/**
 * A set of resources needed for carrying out an basicStep. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, a condition which determines if the 
 * workgroup can be used or not, and the priority of the workgroup inside the basicStep.
 * @author Iván Castilla Rodríguez
 */
public class ActivityWorkGroup extends WorkGroup implements es.ull.iis.simulation.core.ActivityWorkGroup, Comparable<ActivityWorkGroup> {
    /**
	 * 
	 */
	private final RequestResourcesFlow basicStep;
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
     * @param basicStep TODO
     */    
    public ActivityWorkGroup(RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg) {
        this(basicStep, id, priority, wg, new TrueCondition());
    }
    
    /**
     * Creates a new instance of WorkGroup which contains the same resource types
     * than an already existing one.
     * @param id Identifier of this workgroup.
     * @param priority Priority of the workgroup.
     * @param wg The original workgroup
     * @param cond  Availability condition
     * @param basicStep TODO
     */    
    public ActivityWorkGroup(RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond) {
        super(wg.resourceTypes, wg.needed);
		this.basicStep = basicStep;
        this.id = id;
        this.priority = priority;
        this.cond = cond;
        this.idString = new String("(" + this.basicStep + ")" + getDescription());
    }


    /**
     * Returns the basicStep this WG belongs to.
     * @return basicStep this WG belongs to.
     */    
    protected RequestResourcesFlow getBasicStep() {
        return this.basicStep;
    }
    
    /**
     * Getter for property priority.
     * @return Value of property priority.
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Checks if there are enough resources to carry out an basicStep by using this workgroup.   
     * The "potential" available resources are booked by the element requesting the basicStep. 
     * If there are less available resources than needed resources for any resource type, the 
     * basicStep can not be carried out, and all the "books" are removed.
     * Possible conflicts between resources inside the basicStep are solved by invoking a
     * branch-and-bound resource distribution algorithm. 
     * @param wThread Work thread trying to carry out the basicStep with this workgroup 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the basicStep by using this workgroup.
     */
    public ArrayDeque<Resource> isFeasible(WorkThread wThread) {

    	if (!cond.check(wThread.getElement()))
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
        if (findSolution(solution, pos, ned, wThread))
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
    private int []searchNext(int[] pos, int []nec, WorkThread wThread) {
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
    protected boolean findSolution(ArrayDeque<Resource> solution, int []pos, int []ned, WorkThread wThread) {
        pos = searchNext(pos, ned, wThread);
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
        if (findSolution(solution, pos, ned, wThread))
            return true;
        // There's no solution with this resource. Try without it
        unmark(pos, solution);
        ned[pos[0]]++;
        // ... and the search continues
        return findSolution(solution, pos, ned, wThread);        
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