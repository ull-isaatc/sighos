/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.Iterator;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.util.Prioritizable;
import es.ull.iis.util.PrioritizedTable;

/**
 * @author Iván Castilla
 *
 */
public class RequestResourcesFlow extends SingleSuccessorFlow implements TaskFlow, ResourceHandlerFlow, Prioritizable {
    /** Priority. The lowest the value, the highest the priority */
    private final int priority;
    /** A brief description of the activity */
    private final String description;
    /** Work Groups available to perform this basic step */
    private final PrioritizedTable<ActivityWorkGroup> workGroupTable;
    /** A unique identifier that serves to tell a ReleaseResourcesFlow which resources to release */
	private final int resourcesId;
	/** Only one exclusive set of resources can be acquired by an element at the same time */
	private final boolean exclusive;

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Model model, String description, int resourcesId) {
		this(model, description, resourcesId, 0, false);
	}

	/**
	 * @param simul
	 * @param description
	 */
	public RequestResourcesFlow(Model model, String description, int resourcesId, int priority) {
		this(model, description, resourcesId, priority, false);
	}

	/**
	 * @param simul
	 * @param description
	 * @param priority
	 */
	public RequestResourcesFlow(Model model, String description, int resourcesId, int priority, boolean exclusive) {
		super(model);
        this.description = description;
        this.priority = priority;
		this.resourcesId = resourcesId;
		this.exclusive = exclusive;
        workGroupTable = new PrioritizedTable<ActivityWorkGroup>();
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
    public int getPriority() {
        return priority;
    }
	
	/**
	 * @return the exclusive
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * @return the resourcesId
	 */
	public int getResourcesId() {
		return resourcesId;
	}

	/**
	 * Allows a user for adding a customized code when a {@link es.ull.iis.simulation.model.flow.FlowExecutor} from an {@link es.ull.iis.simulation.model.Element}
	 * is enqueued, waiting for available {@link es.ull.iis.simulation.model.Resource}. 
	 * @param wt {@link es.ull.iis.simulation.model.flow.FlowExecutor} requesting resources
	 */
	public void inqueue(FlowExecutor fe) {}
	
	
	/**
     * Creates a new workgroup for this activity using the specified wg.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg) {
    	return addWorkGroup(priority, wg, new TrueCondition(), 0L);
    }
    
    /**
     * Creates a new workgroup for this activity using the specified wg. This workgroup
     * is only available if cond is true.
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond) {
    	return addWorkGroup(priority, wg, cond, 0L);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg) {    	
    	return addWorkGroup(0, wg, new TrueCondition(), 0L);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority using 
     * the specified wg. This workgroup is only available if cond is true.
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(WorkGroup wg, Condition cond) {    	
    	return addWorkGroup(0, wg, cond, 0L);
    }
	
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond, TimeFunction duration) {
    	final int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(model, this, wgId, priority, wg, cond, duration));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(int priority, WorkGroup wg, TimeFunction duration) {
		return addWorkGroup(priority, (WorkGroup)wg, new TrueCondition(), duration);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, long duration) {
        return addWorkGroup(priority, wg, TimeFunctionFactory.getInstance("ConstantVariate", duration));
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond, long duration) {    	
        return addWorkGroup(priority, wg, cond, TimeFunctionFactory.getInstance("ConstantVariate", duration));
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        Iterator<ActivityWorkGroup> iter = workGroupTable.iterator();
        while (iter.hasNext()) {
        	ActivityWorkGroup opc = iter.next();
        	if (opc.getIdentifier() == wgId)
        		return opc;        	
        }
        return null;
    }
	
	/**
	 * Returns the amount of WGs associated to this activity
	 * @return the amount of WGs associated to this activity
	 */
	public int getWorkGroupSize() {
		return workGroupTable.size();
	}

    /**
     * Returns an iterator over the WGs of this activity.
     * @return An iterator over the WGs that can perform this activity.
     */
    public Iterator<ActivityWorkGroup> iterator() {
    	return workGroupTable.iterator();
    }

	@Override
	public void addPredecessor(Flow newFlow) {}
    
	/**
	 * Returns true if this delay flow is being used as part of an interruptible activity
	 * @return True if this delay flow is being used as part of an interruptible activity
	 */
	public boolean partOfInterruptible() {
		if (parent != null)
			if (parent instanceof ActivityFlow)
				return ((ActivityFlow)parent).isInterruptible();
		return false;
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "ACQ";
	}
	
	/**
	 * Allows a user for adding a customized code when the {@link WorkThread} actually starts the
	 * execution of the {@link ActivityFlow}.
	 * @param fe {@link FlowExecutor} requesting this {@link ActivityFlow}
	 */
	public void afterStart(FlowExecutor fe) {
	}

	@Override
	public void afterFinalize(FlowExecutor fe) {
	}

}
