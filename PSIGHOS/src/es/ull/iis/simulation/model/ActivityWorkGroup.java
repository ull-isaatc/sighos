package es.ull.iis.simulation.model;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.Describable;
import es.ull.iis.simulation.core.Identifiable;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import es.ull.iis.simulation.model.flow.FinalizerFlow;
import es.ull.iis.simulation.model.flow.InitializerFlow;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow;
import es.ull.iis.util.Prioritizable;

/**
 * A set of resources needed for carrying out an basicStep. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, a condition which determines if the 
 * workgroup can be used or not, and the priority of the workgroup inside the basicStep.
 * @author Iván Castilla Rodríguez
 */
public class ActivityWorkGroup extends WorkGroup implements Prioritizable, Describable, Identifiable, Comparable<ActivityWorkGroup> {
	public enum DrivenBy {
		TIME,
		FLOW,
		NONE
	}
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
    /** The type of the activity WG according to its driver */
    private final DrivenBy drivenBy; 
	/** Duration of the activity when using this WG */
    final protected TimeFunction duration;
	/** The first step of the subflow */
    final protected InitializerFlow initialFlow;
    /** The last step of the subflow */
    final protected FinalizerFlow finalFlow;
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
    public ActivityWorkGroup(RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond) {
    	this(basicStep, id, priority, wg, cond, null, null, null, DrivenBy.NONE);
    }

    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param duration Duration of the activity when using this WG.
     * @param priority Priority of the workgroup.
     * @param cond  Availability condition
     * @param timeDrivenActivity TODO
     */    
    public ActivityWorkGroup(ActivityFlow timeDrivenActivity, int id, TimeFunction duration, int priority, WorkGroup wg, Condition cond) {
        this(timeDrivenActivity, id, priority, wg, cond, duration, null, null, DrivenBy.TIME);
    }

    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param initialFlow Initial Flow
     * @param finalFlow Final Flow
     * @param priority Priority of the workgroup.
     * @param wg WorkGroup
     * @param cond  Availability condition
     * @param flowDrivenActivity TODO
     */    
    public ActivityWorkGroup(ActivityFlow flowDrivenActivity, int id, InitializerFlow initialFlow, FinalizerFlow finalFlow, 
    		int priority, WorkGroup wg, Condition cond) {
        this(flowDrivenActivity, id, priority, wg, cond, null, initialFlow, finalFlow, DrivenBy.FLOW);
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
    public ActivityWorkGroup(RequestResourcesFlow basicStep, int id, int priority, WorkGroup wg, Condition cond, TimeFunction duration, 
    		InitializerFlow initialFlow, FinalizerFlow finalFlow, DrivenBy drivenBy) {
        super(wg.pairs);
		this.basicStep = basicStep;
        this.id = id;
        this.priority = priority;
        this.cond = cond;
        this.idString = new String("(" + this.basicStep + ")" + getDescription());
        this.drivenBy = drivenBy;
        this.duration = duration;
        this.initialFlow = initialFlow;
        this.finalFlow = finalFlow;
    }

    /**
	 * @return the drivenBy
	 */
	public DrivenBy getDrivenBy() {
		return drivenBy;
	}

	/**
     * Returns the duration of the activity where this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.0.
     * @return The activity duration.
     */
    public TimeFunction getDuration() {
        return duration;
    }
    
    /**
     * Returns the first step of the subflow
	 * @return the initialFlow
	 */
	public InitializerFlow getInitialFlow() {
		return initialFlow;
	}

	/**
     * Returns the last step of the subflow
	 * @return the finalFlow
	 */
	public FinalizerFlow getFinalFlow() {
		return finalFlow;
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