package es.ull.iis.simulation.model;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.Describable;
import es.ull.iis.simulation.core.Identifiable;
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
     * @param cond  Availability condition
     * @param basicStep TODO
     */    
    public ActivityWorkGroup(Model model, RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond) {
        super(model, wg.pairs);
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

    @Override
	public int getIdentifier() {
		return id;
	}

	public String getDescription() {
		StringBuilder str = new StringBuilder("WG" + id);
		for (Pair pair : pairs)
			str.append(" [" + pair.rt + "," + pair.needed + "]");
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

	public Condition getCondition() {
		return cond;
	}

}