/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A set of pairs &lt{@link ResourceType}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link ActivityFlow}).
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup extends SimulationObject implements Describable {
	protected final ResourceType[] resourceTypes;
	protected final int[] needed;
		
	/**
	 * 
	 */
	public WorkGroup(Simulation model) {
		this(model, new ResourceType[0], new int[0]);
	}

    /**
     * Creates a new instance of WorkGroup initializing the list of pairs
     * <resource type, needed resources> with one pair. 
     * @param rt Resource Type
     * @param needed Resources needed
     */    
    public WorkGroup(Simulation model, ResourceType rt, int needed) {
        this(model, new ResourceType[] {rt}, new int[] {needed});
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * <resource type, needed resources>.
     * @param rts The resource types which compounds this WG.
     * @param needs The amounts of resource types required by this WG.
     */    
    public WorkGroup(Simulation model, ResourceType[] rts, int []needs) {
    	super(model, model.getWorkGroupList().size(), "WG");
    	this.resourceTypes = rts;
    	this.needed = needs;
		model.add(this);
    }

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return resourceTypes.length;
    }
    
    /**
     * Returns the resource type from the position ind of the table.
     * @param ind Index of the entry
     * @return The resource type from the position ind. 
     */
    public ResourceType getResourceType(int ind) {
        return resourceTypes[ind];
    }

    /**
     * Returns the needed amount of resources from the position ind of the table.
     * @param ind Index of the entry
     * @return The needed amount of resources from the position ind. 
     */
    public int getNeeded(int ind) {
        return needed[ind];
    }

    public int[] getNeeded() {
    	return needed;    	
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

	public String getDescription() {
		StringBuilder str = new StringBuilder("WG" + id);
		for (int i = 0; i < resourceTypes.length; i++)
			str.append(" [" + resourceTypes[i] + "," + needed[i] + "]");
		return str.toString();
	}
    
	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// TODO Auto-generated method stub
		
	}
}
