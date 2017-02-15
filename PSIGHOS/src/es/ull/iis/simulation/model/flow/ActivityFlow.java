/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.EnumSet;
import java.util.TreeMap;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ActivityWorkGroup;
import es.ull.iis.simulation.model.Model;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.WorkGroup;

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
public class ActivityFlow extends RequestResourcesFlow implements TaskFlow {
	private static int resourcesIdCounter = -1;

	/** Indicates special characteristics of this activity */
	enum Modifier {
	    /** Indicates that this activity is non presential, i.e., an element can perform other activities at
	     * the same time */
		NONPRESENTIAL,
		/** Indicates that the activity can be interrupted in case the required resources end their
		 * availability time */
		INTERRUPTIBLE
	}
	
	/** The set of modifiers of this activity. */
    protected final EnumSet<Modifier> modifiers;
    /** Resources cancellation table */
    protected final TreeMap<ResourceType, Long> cancellationList;

	/**
     * Creates a new activity with 0 priority.
     * @param description A short text describing this Activity.
     */
    public ActivityFlow(String description) {
        this(description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public ActivityFlow(String description, int priority) {
        this(description, priority, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(String description, EnumSet<Modifier> modifiers) {
        this(description, 0, modifiers);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(String description, int priority, EnumSet<Modifier> modifiers) {
        super(description, resourcesIdCounter--, priority);
        this.modifiers = modifiers;
		cancellationList = new TreeMap<ResourceType, Long>();
    }

	/**
	 * Returns the set of modifiers assigned to this activity.
	 * @return The set of modifiers assigned to this activity
	 */
	public EnumSet<Modifier> getModifiers() {
		return modifiers;
	}

	/** 
	 * Returns <tt>true</tt> if the activity is non presential, i.e., an element can perform other 
	 * activities at the same time. 
	 * @return <tt>True</tt> if the activity is non presential, <tt>false</tt> in other case.
	 */
    public boolean isNonPresential() {
        return modifiers.contains(Modifier.NONPRESENTIAL);
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
		return modifiers.contains(Modifier.INTERRUPTIBLE);
	}
	
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    public ActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, WorkGroup wg, Condition cond) {
		ActivityWorkGroup aWg = new ActivityWorkGroup(this, workGroupTable.size(), duration, priority, wg, cond); 
        workGroupTable.add(aWg);
        return aWg;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    public ActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, WorkGroup wg) {
		return addWorkGroup(duration, priority, (WorkGroup)wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    public ActivityWorkGroup addWorkGroup(long duration, int priority, WorkGroup wg) {
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    public ActivityWorkGroup addWorkGroup(long duration, int priority, WorkGroup wg, Condition cond) {    	
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg, cond);
    }

    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    public ActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg, Condition cond) {
    	ActivityWorkGroup aWg = new ActivityWorkGroup(this, workGroupTable.size(), initFlow, finalFlow, priority, (WorkGroup)wg, cond);
		workGroupTable.add(aWg);
		// Activities with Flow-driven workgroups cannot be presential nor interruptible
		modifiers.add(Modifier.NONPRESENTIAL);
		if (modifiers.contains(Modifier.INTERRUPTIBLE)) {
			Model.error(this + "\tTrying to add a flow-driven workgroup to an interruptible activity. This attribute will be overriden to ensure proper functioning");
			modifiers.remove(Modifier.INTERRUPTIBLE);
		}
		return aWg;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    public ActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg) {
    	return addWorkGroup(initFlow, finalFlow, priority, wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    public ActivityWorkGroup addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, WorkGroup wg) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */
    public ActivityWorkGroup addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, WorkGroup wg, Condition cond) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg, cond);
    }
    
	/**
	 * Adds a new ResouceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(ResourceType rt, long duration) {
		cancellationList.put(rt, duration);
	}
	
	/**
	 * @return the cancellationList
	 */
	public long getResourceCancellation(ResourceType rt) {
		final Long duration = cancellationList.get(rt); 
		if (duration == null)
			return 0;
		return duration;
	}
	
	/**
	 * @return the cancellationList
	 */
	public TreeMap<ResourceType, Long> getCancellationList() {
		return cancellationList;
	}

	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}
	
	/**
	 * Allows a user for adding a customized code when the {@link WorkThread} actually starts the
	 * execution of the {@link ActivityFlow}.
	 * @param fe {@link FlowExecutor} requesting this {@link ActivityFlow}
	 */
	public void afterStart(FlowExecutor fe) {
	}
}
