package es.ull.iis.simulation.sequential;

import java.util.ArrayDeque;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.flow.FlowExecutor;
import es.ull.iis.util.Prioritizable;

/**
 * A set of resources needed for carrying out an basicStep. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, a condition which determines if the 
 * workgroup can be used or not, and the priority of the workgroup inside the basicStep.
 * @author Iván Castilla Rodríguez
 */
public class ActivityWorkGroupEngine implements es.ull.iis.simulation.model.ActivityWorkGroupEngine {
    protected final es.ull.iis.simulation.model.ActivityWorkGroup modelAWG;
	
    /**
     * Creates a new instance of WorkGroup which contains the same resource types
     * than an already existing one.
     * @param id Identifier of this workgroup.
     * @param priority Priority of the workgroup.
     * @param wg The original workgroup
     * @param cond  Availability condition
     * @param basicStep TODO
     */    
    public ActivityWorkGroupEngine(SequentialSimulationEngine simul, es.ull.iis.simulation.model.ActivityWorkGroup modelAWG) {
        this.modelAWG = modelAWG;
    }

    /**
	 * @return the modelAWG
	 */
	public es.ull.iis.simulation.model.ActivityWorkGroup getModelAWG() {
		return modelAWG;
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
    public ArrayDeque<ResourceEngine> isFeasible(WorkThread wThread) {

    	if (!getCondition().check(wThread))
    		return null;

    	int ned[] = needed.clone();
    	if (ned.length == 0) // Infinite resources
    		return new ArrayDeque<ResourceEngine>(); 
        int []pos = {0, -1}; // "Start" position
        
        int totalRes = 0;
        for (int n : ned)
            totalRes += n;
        ArrayDeque<ResourceEngine> solution = new ArrayDeque<ResourceEngine>(totalRes);
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
        ResourceTypeEngine rt = resourceTypes[aux[0]];
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
    private void mark(int []pos, ArrayDeque<ResourceEngine> solution) {
        ResourceEngine res = resourceTypes[pos[0]].getResource(pos[1]);
        res.setCurrentResourceType(resourceTypes[pos[0]]);
        solution.push(res);
    }
    
    /**
     * Removes the mark of a resource as belonging to the solution
     * @param pos Position [ResourceType, Resource] of the resource
     */
    private void unmark(int []pos, ArrayDeque<ResourceEngine> solution) {
        ResourceEngine res = resourceTypes[pos[0]].getResource(pos[1]);
        res.setCurrentResourceType(null);
        solution.pop();
    }

    /**
     * Makes a depth first search looking for a solution.
     * @param pos Position to look for a solution [ResourceType, Resource] 
     * @param ned Resources needed
     * @return True if a valid solution exists. False in other case.
     */
    protected boolean findSolution(ArrayDeque<ResourceEngine> solution, int []pos, int []ned, WorkThread wThread) {
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
    
    

}