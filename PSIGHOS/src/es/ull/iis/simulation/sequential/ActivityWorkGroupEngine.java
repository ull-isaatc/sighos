package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.model.FlowExecutor;

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
	@Override
	public boolean isFeasible(FlowExecutor fe) {
    	if (!modelAWG.getCondition().check(fe))
    		return false;

    	int ned[] = modelAWG.getNeeded().clone();
    	if (ned.length == 0) // Infinite resources
    		return true; 
        int []pos = {0, -1}; // "Start" position
        
        // B&B algorithm for finding a solution
        return modelAWG.findSolution(pos, ned, fe);
	}
    
    

}