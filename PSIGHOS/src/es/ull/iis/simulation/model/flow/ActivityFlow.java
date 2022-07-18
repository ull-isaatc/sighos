/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.ElementInstance;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow.ActivityWorkGroup;
import es.ull.iis.simulation.model.flow.RequestResourcesFlow.WorkGroupAdder;
import es.ull.iis.util.Prioritizable;

/**
 * A flow which executes a single activity. An activity is characterized by a priority and a set of {@link WorkGroup workgropus}. 
 * Each workgroup represents a combination of resource types required for carrying out the activity, and also defines different workgroup-specific
 * durations of the activity.<p>
 * 
 * By default, activities are exclusive, that is, an element carrying out this activity can't perform simultaneously any other exclusive
 * activity; and ininterruptible, i.e., once started, the activity keeps its resources until it's finished, even if the resources become 
 * unavailable while the activity is being performed. These characteristics can be modified by means of different constructors.<p>
 * 
 * When an element requests an activity, it checks whether there is a least one workgroup with enough available resources to perform the activity. 
 * The condition for the workgroup has to be met too.<p>
 * 
 * After performing the activity, some resources can be "cancelled", i.e., can become unavailable during certain amount of time. The cancellation can 
 * be condition-driven. 
 * 
 * @author Iván Castilla Rodríguez
 */
public class ActivityFlow extends StructuredFlow implements ResourceHandlerFlow, Prioritizable {
	private static int resourcesIdCounter = -1;

    /** Priority. The lowest the value, the highest the priority */
    protected final int priority;
    /** A brief description of the activity */
    protected final String description;
	/** If true, the activity is interrupted when any resource in use becomes unavailable */
    private boolean interruptible;
    /** A unique identifier that serves to tell a ReleaseResourcesFlow which resources to release */
	private final int resourcesId;
	/** If true, an element cannot perform any other exclusive activity concurrently with this one */
    private boolean exclusive;
    

	/**
     * Creates a new exclusive and non-interruptible activity with the highest priority.
     * @param model The simulation model this activity belongs to
     * @param description A short text describing this activity.
     */
    public ActivityFlow(final Simulation model, final String description) {
        this(model, description, 0, true, false);
    }

    /**
     * Creates a new exclusive and non-interruptible activity with the specified priority.
     * @param model The simulation model this activity belongs to
     * @param description A short text describing this activity.
     * @param priority Activity's priority.
     */
    public ActivityFlow(final Simulation model, final String description, final int priority) {
        this(model, description, priority, true, false);
    }

    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param model The simulation model this activity belongs to
     * @param description A short text describing this activity.
     * @param exclusive If true, this activity cannot be performed concurrently with any other exclusive activity
     * @param interruptible If true, this activity is interrupted when the involved resources become unavailable
     */
    public ActivityFlow(final Simulation model, final String description, final boolean exclusive, final boolean interruptible) {
        this(model, description, 0, exclusive, interruptible);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param model The simulation model this activity belongs to
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param exclusive If true, this activity cannot be performed concurrently with any other exclusive activity
     * @param interruptible If true, this activity is interrupted when the involved resources become unavailable
     */
    public ActivityFlow(final Simulation model, final String description, final int priority, final boolean exclusive, final boolean interruptible) {
    	super(model);
    	this.priority = priority;
    	this.description = description;
        this.exclusive = exclusive;
        this.interruptible = interruptible;
    	resourcesId = resourcesIdCounter--;
        initialFlow = new RequestResourcesFlow(model, description, resourcesId , priority);
        initialFlow.setParent(this);
        finalFlow = new ReleaseResourcesFlow(model, description, resourcesId);
        finalFlow.setParent(this);
        initialFlow.link(finalFlow);
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
	 * Creates a builder object for adding workgroups to this flow. 
	 * @param wg The set of pairs <ResurceType, amount> which will be seized
	 * @return The builder object for adding workgroups to this flow
	 */
	public WorkGroupAdder newWorkGroupAdder(final WorkGroup wg) {
		return ((RequestResourcesFlow)initialFlow).newWorkGroupAdder(wg);
	}
	
    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(final int wgId) {
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
	 * Adds a ResourceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 */
	public void addResourceCancellation(final ResourceType rt, final long duration) {
		((ReleaseResourcesFlow)finalFlow).addResourceCancellation(rt, duration);
	}
	
	/**
	 * Adds a ResourceType to the cancellation list.
	 * @param rt Resource type
	 * @param duration Duration of the cancellation.
	 * @param cond Condition that must be fulfilled to apply the cancellation 
	 */
	public void addResourceCancellation(final ResourceType rt, final long duration, final Condition<ElementInstance> cond) {
		((ReleaseResourcesFlow)finalFlow).addResourceCancellation(rt, duration, cond);
	}
	
	@Override
	public String getObjectTypeIdentifier() {
		return "ACT";
	}

	@Override
	public void finish(final ElementInstance ei) {
		if (ei.wasInterrupted(this)) {
			request(ei);
		}
		else {
			if (isExclusive()) {
				ei.getElement().setExclusive(false);
			}			
			super.finish(ei);
		}
		
	}

	@Override
	public int getResourcesId() {
		return resourcesId;
	}

}
