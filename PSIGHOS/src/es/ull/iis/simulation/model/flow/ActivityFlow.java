/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.util.Prioritizable;

/**
 * A flow which executes a single activity.
 * 
 *  TODO: Fix documentation... From here on belongs to the old "activity" class
 * A task which could be carried out by an element. An activity is characterized by its priority
 * and a set of workgropus. Each workgroup represents a combination of resource types required 
 * for carrying out the activity.<p>
 * Each activity belongs to an Activity Manager, which handles the way the activity is accessed.<p>
 * An activity is potentially feasible if there is no proof that there are not enough resources
 * to perform it. An activity is feasible if it's potentially feasible and there is at least one
 * workgroup with enough available resources to perform the activity.<p>
 * An activity can be requested by a valid element, that is, check if the activity is feasible. 
 * If the activity is not feasible, the element is added to a queue until new resources are 
 * available. If the activity is feasible, the element "carries out" the activity, that is, 
 * catches the resources needed to perform the activity. Whenever it is determined that the 
 * activity has finished, the element releases the resources previously caught.<p>
 * An activity can also define cancellation periods for each one of the resource types it uses. 
 * If an element takes a resource belonging to one of the cancellation periods of the activity, this
 * resource can't be used during a period of time after the activity finishes.
 * FIXME: Complete and rewrite (original description for TimeDrivenActivities)
 *  A task which could be carried out by an element in a specified time. This kind of activities
 * can be characterized by a priority value, presentiality, interruptibility, and a set of 
 * workgropus. Each workgroup represents a combination of resource types required for carrying out 
 * the activity, and the duration of the activity when performed with this workgroup.<p>
 * By default, time-driven activities are presential, that is, an element carrying out this 
 * activity can't perform simultaneously any other presential activity; and ininterruptible, i.e., 
 * once started, the activity keeps its resources until it's finished, even if the resources become 
 * unavailable while the activity is being performed. This two characteristics are customizable by 
 * means of the <code>Modifier</code> enum type. An activity can be <code>NONPRESENTIAL</code>, when 
 * the element can perform other activities while it's performing this one; and <code>INTERRUPTIBLE</code>, 
 * when the activity can be interrupted, and later continued, if the resources become unavailable 
 * while the activity is being performed.
 * @author Iván Castilla Rodríguez
 */
public class ActivityFlow extends StructuredFlow implements ResourceHandlerFlow, Prioritizable {
	private static int resourcesIdCounter = -1;

    /** Priority. The lowest the value, the highest the priority */
    protected final int priority;
    /** A brief description of the activity */
    protected final String description;
	/** The set of modifiers of this activity. */
    private boolean interruptible;
    /** A unique identifier that serves to tell a ReleaseResourcesFlow which resources to release */
	private final int resourcesId;
    private boolean exclusive;
    

	/**
     * Creates a new activity with 0 priority.
     * @param description A short text describing this Activity.
     */
    public ActivityFlow(Simulation model, String description) {
        this(model, description, 0, true, false);
    }

    /**
     * Creates a new activity.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public ActivityFlow(Simulation model, String description, int priority) {
        this(model, description, priority, true, false);
    }

    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(Simulation model, String description, boolean exclusive, boolean interruptible) {
        this(model, description, 0, exclusive, interruptible);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(Simulation model, String description, int priority, boolean exclusive, boolean interruptible) {
    	super(model);
    	this.priority = priority;
    	this.description = description;
    	resourcesId = resourcesIdCounter--;
        initialFlow = new RequestResourcesFlow(model, description, resourcesId , priority, exclusive);
        initialFlow.setParent(this);
        finalFlow = new ReleaseResourcesFlow(model, description, resourcesId);
        finalFlow.setParent(this);
        initialFlow.link(finalFlow);
        this.exclusive = exclusive;
        this.interruptible = interruptible;
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
	 * Returns <tt>true</tt> if the activity is exclusive, i.e., an element cannot perform other 
	 * exclusive activities at the same time. 
	 * @return <tt>True</tt> if the activity is exclusive, <tt>false</tt> in other case.
	 */
    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * Returns <tt>true</tt> if this activity is interruptible, i.e., the activity is
     * suspended when any of the the resources taken to perform the activity finalize 
     * their availability. The activity can be resumed when there are available resources 
     * again (<b>but not necessarily the same resources</b>). 
     * <p>By default, an activity is not interruptible.  
     * @return Always <tt>false</tt>. Subclasses overriding this method must change the 
     * default behavior. 
     */
	public boolean isInterruptible() {
		return interruptible;
	}

    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, Condition cond, TimeFunction duration) {
		return ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, cond, duration);
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
		return ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, duration);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(int priority, WorkGroup wg, long duration) {
        return ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, duration);
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
        return ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, cond, duration);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        return ((RequestResourcesFlow)initialFlow).getWorkGroup(wgId);
    }
	
	/**
	 * Returns the amount of WGs associated to this activity
	 * @return the amount of WGs associated to this activity
	 */
	public int getWorkGroupSize() {
		return ((RequestResourcesFlow)initialFlow).getWorkGroupSize();
	}

	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceType rt, long duration) {
		((ReleaseResourcesFlow)finalFlow).addResourceCancellation(rt, duration);
	}
	
	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 * @param cond Condition that must be fulfilled to apply the cancellation 
	 */
	public void addResourceCancellation(ResourceType rt, long duration, Condition cond) {
		((ReleaseResourcesFlow)finalFlow).addResourceCancellation(rt, duration, cond);
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	@Override
	public void finish(ElementInstance wThread) {
		if (wThread.wasInterrupted(this)) {
			request(wThread);
		}
		else {
			super.finish(wThread);
		}
		
	}

	@Override
	public int getResourcesId() {
		return resourcesId;
	}

}
