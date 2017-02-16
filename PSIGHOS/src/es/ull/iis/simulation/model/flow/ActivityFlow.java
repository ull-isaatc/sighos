/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.model.ResourceType;
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

	protected class WGCondition extends Condition {
		final int wgId;
		public WGCondition(int wgId) {
			super();
			this.wgId = wgId;
		}		
		
		@Override
		public boolean check(FlowExecutor fe) {			
			return (fe.getExecutionWG().getIdentifier() == wgId);
		}
	}
    /** Priority. The lowest the value, the highest the priority */
    protected final int priority;
    /** A brief description of the activity */
    protected final String description;
	/** The set of modifiers of this activity. */
    private boolean interruptible;
    private boolean exclusive;
    /** Resources cancellation table */
    private final TreeMap<ResourceType, Long> cancellationList;
    private final ExclusiveChoiceFlow selectWorkGroupFlow;

	/**
     * Creates a new activity with 0 priority.
     * @param description A short text describing this Activity.
     */
    public ActivityFlow(String description) {
        this(description, 0, true, false);
    }

    /**
     * Creates a new activity.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public ActivityFlow(String description, int priority) {
        this(description, priority, true, false);
    }

    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(String description, boolean exclusive, boolean interruptible) {
        this(description, 0, exclusive, interruptible);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public ActivityFlow(String description, int priority, boolean exclusive, boolean interruptible) {
    	super();
    	this.priority = priority;
    	this.description = description;
    	final int resId = resourcesIdCounter--;
        initialFlow = new RequestResourcesFlow("REQ " + description, resId , priority, exclusive) {
        	@Override
        	public void afterFinalize(FlowExecutor fe) {
        		afterStart(fe);
        	}
        };
        initialFlow.setParent(this);
        selectWorkGroupFlow = new ExclusiveChoiceFlow();
        selectWorkGroupFlow.setParent(this);
        initialFlow.link(selectWorkGroupFlow);
        finalFlow = new ReleaseResourcesFlow("REL " + description, resId);
        finalFlow.setParent(this);
        this.exclusive = exclusive;
        this.interruptible = interruptible;
		cancellationList = new TreeMap<ResourceType, Long>();
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
	 * activities at the same time. 
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
    public int addWorkGroup(TimeFunction duration, int priority, WorkGroup wg, Condition cond) {
    	final int wgId = ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, cond);
    	final DelayFlow delayFlow = new DelayFlow("WG" + wgId + "_DELAY " + description, duration, interruptible);
    	delayFlow.setParent(this);
    	selectWorkGroupFlow.link(delayFlow, new WGCondition(wgId));
    	delayFlow.link(finalFlow);
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
    public int addWorkGroup(TimeFunction duration, int priority, WorkGroup wg) {
		return addWorkGroup(duration, priority, (WorkGroup)wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(long duration, int priority, WorkGroup wg) {
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg);
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(long duration, int priority, WorkGroup wg, Condition cond) {    	
        return addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", duration), priority, wg, cond);
    }

    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg, Condition cond) {
    	final int wgId = ((RequestResourcesFlow)initialFlow).addWorkGroup(priority, wg, cond);
		final TreeSet<Flow> visited = new TreeSet<Flow>(); 
    	initFlow.setRecursiveStructureLink(parent, visited);
    	selectWorkGroupFlow.link(initFlow, new WGCondition(wgId));
    	finalFlow.link(this.finalFlow);
    	
		// Activities with Flow-driven workgroups cannot be presential nor interruptible
    	exclusive = false;
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */    
    public int addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg) {
    	return addWorkGroup(initFlow, finalFlow, priority, wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, WorkGroup wg) {    	
        return addWorkGroup(initialFlow, finalFlow, 0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(InitializerFlow initialFlow, FinalizerFlow finalFlow, WorkGroup wg, Condition cond) {    	
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
