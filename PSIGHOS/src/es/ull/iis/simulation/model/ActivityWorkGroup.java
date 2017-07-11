package es.ull.iis.simulation.model;

import java.util.ArrayDeque;

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
public class ActivityWorkGroup implements Prioritizable, Identifiable, Describable {
    /**
	 * 
	 */
	final protected RequestResourcesFlow basicStep;
	/** Priority of the workgroup */
    final protected int priority;
    /** Availability condition */
    final protected Condition cond;
    final protected TimeFunction duration;
    final protected WorkGroup wg;
    /** Precomputed string which identifies this WG */
    private final String idString; 
	
    /**
     * Creates a new instance of WorkGroup which contains the same resource types
     * than an already existing one.
     * @param id Identifier of this workgroup.
     * @param priority Priority of the workgroup.
     * @param wg The original workgroup
     * @param cond  Availability condition
     * @param basicStep TODO
     */    
    public ActivityWorkGroup(Simulation model, RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond, TimeFunction duration) {
		this.basicStep = basicStep;
        this.priority = priority;
        this.cond = cond;
        this.duration = duration;
        this.wg = wg;
        this.idString = new String("(" + this.basicStep + ")" + wg.getDescription());
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

	@Override
	public int getIdentifier() {
		return wg.getIdentifier();
	}

	@Override
	public String getDescription() {
		return wg.getDescription();
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
    public long getDurationSample(ElementInstance fe) {
    	return Math.round(duration.getValue(fe));
    }

    public ResourceType getResourceType(int ind) {
    	return wg.getResourceType(ind);
    }
    
    public int[] getNeeded() {
    	return wg.getNeeded();
    }
    
    public int size() {
    	return wg.size();
    }
    @Override
    public String toString() {
    	return idString;
    }

	public Condition getCondition() {
		return cond;
	}
	
    /**
     * Checks if there are enough resources to carry out an basicStep by using this workgroup.   
     * The "potential" available resources are booked by the element requesting the basicStep. 
     * If there are less available resources than needed resources for any resource type, the 
     * basicStep can not be carried out, and all the "books" are removed.
     * Possible conflicts between resources inside the basicStep are solved by invoking a
     * branch-and-bound resource distribution algorithm. 
	 * @param solution Tentative solution with booked resources
     * @param wThread Work thread trying to carry out the basicStep with this workgroup 
     * @return The set of resources which compound the solution. Null if there are not enough
     * resources to carry out the basicStep by using this workgroup.
     */
	public boolean findSolution(ArrayDeque<Resource> solution, int []pos, int []ned, ElementInstance ei) {
        return wg.findSolution(solution, pos, ned, ei);
	}

}