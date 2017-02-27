package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.Prioritizable;

/**
 * A set of resources needed for carrying out an basicStep. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, a condition which determines if the 
 * workgroup can be used or not, and the priority of the workgroup inside the basicStep.
 * @author Iván Castilla Rodríguez
 */
public class ActivityWorkGroup extends WorkGroup implements Prioritizable, Describable, Identifiable {
    /**
	 * 
	 */
	final protected RequestResourcesFlow basicStep;
	/** Workgroup's identifier */
	final protected int id;
	/** Priority of the workgroup */
    final protected int priority;
    /** Availability condition */
    final protected Condition cond;
    final protected TimeFunction duration;
    private final String idString; 
    private ActivityWorkGroupEngine wgEngine;
	
    /**
     * Creates a new instance of WorkGroup which contains the same resource types
     * than an already existing one.
     * @param id Identifier of this workgroup.
     * @param priority Priority of the workgroup.
     * @param wg The original workgroup
     * @param cond  Availability condition
     * @param basicStep TODO
     */    
    public ActivityWorkGroup(Model model, RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond, TimeFunction duration) {
        super(model, wg.resourceTypes, wg.needed);
		this.basicStep = basicStep;
        this.id = id;
        this.priority = priority;
        this.cond = cond;
        this.duration = duration;
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
	 * @return the duration
	 */
	public TimeFunction getDuration() {
		return duration;
	}

    /**
     * Returns the duration of the activity where this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.
     * @return The activity duration.
     */
    public long getDurationSample(FlowExecutor fe) {
    	return Math.round(duration.getValue(fe));
    }

	@Override
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

	public Condition getCondition() {
		return cond;
	}
	
	public boolean isFeasible(FlowExecutor fe) {
		return wgEngine.isFeasible(fe);
	}

    /**
     * Checks if a valid solution can be reached from the current situation. This method 
     * is used to bound the search tree.
     * @param pos Initial position.
     * @param nec Resources needed.
     * @return True if there is a reachable solution. False in other case.
     */
    protected boolean hasSolution(int []pos, int []nec, FlowExecutor fe) {
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
    private int []searchNext(int[] pos, int []nec, FlowExecutor fe) {
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
        aux[1] = resourceTypes[aux[0]].getNextAvailableResource(aux[1] + 1, fe);
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
    public boolean findSolution(int []pos, int []ned, FlowExecutor fe) {
        pos = searchNext(pos, ned, fe);
        // No solution
        if (pos == null)
            return false;
        // No more elements needed => SOLUTION
        if (pos[0] == resourceTypes.length)
            return true;
        ned[pos[0]]--;
        // Bound
        if (hasSolution(pos, ned, fe))
        // ... the search continues
            if (findSolution(pos, ned, fe))
                return true;
        // There's no solution with this resource. Try without it
        final Resource res = resourceTypes[pos[0]].getResource(pos[1]);
        res.removeFromSolution(fe);
        ned[pos[0]]++;
        // ... and the search continues
        return findSolution(pos, ned, fe);        
    }
    
	
	@Override
	protected void assignSimulation(SimulationEngine simul) {
		super.assignSimulation(simul);
		wgEngine = simul.getActivityWorkGroupEngineInstance(this);		
	}
}