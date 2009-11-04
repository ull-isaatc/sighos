package es.ull.isaatc.simulation.model;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivityWorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.TrueCondition;

/**
 * A task which could be carried out by an element in a specified time. This kind of activities
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
public class TimeDrivenActivity extends Activity implements es.ull.isaatc.simulation.common.TimeDrivenActivity {
	/** The set of modifiers of this activity. */
    protected final EnumSet<Modifier> modifiers;

	/**
     * Creates a new activity with the highest priority and default behavior.
     * @param id Activity's identifier
     * @param Model model which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public TimeDrivenActivity(int id, Model model, String description) {
        this(id, model, description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity with the specified priority and default behavior.
     * @param id Activity's identifier.
     * @param Model model which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public TimeDrivenActivity(int id, Model model, String description, int priority) {
        this(id, model, description, priority, EnumSet.noneOf(Modifier.class));
    }
    
    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param id Activity's identifier.
     * @param Model model which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public TimeDrivenActivity(int id, Model model, String description, EnumSet<Modifier> modifiers) {
        this(id, model, description, 0, modifiers);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param id Activity's identifier.
     * @param Model model which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public TimeDrivenActivity(int id, Model model, String description, int priority, EnumSet<Modifier> modifiers) {
        super(id, model, description, priority);
        this.modifiers = modifiers;
    }

    /**
     * Indicates if the activity requires the presence of the element in order to be carried out. 
     * @return The "presenciality" of the activity.
     */
    public boolean isNonPresential() {
        return modifiers.contains(Modifier.NONPRESENTIAL);
    }
    
    /**
     * Indicates if the activity can be interrupted in case the required resources end their
     * availability time.
	 * @return True if the activity can be interrupted. False if it keeps the resources even 
	 * if they become not available.
	 */
	@Override
	public boolean isInterruptible() {
		return modifiers.contains(Modifier.INTERRUPTIBLE);
	}
	
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, int priority, es.ull.isaatc.simulation.model.WorkGroup wg) {
    	return addWorkGroup(duration, priority, wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, duration, priority, wg, cond));
        return wgCounter++;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, es.ull.isaatc.simulation.model.WorkGroup wg) {    	
        return addWorkGroup(duration, 0, wg, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(duration, 0, wg, cond);
    }

    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, int priority) {
        return addWorkGroup(duration, priority, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, int priority, Condition cond) {
        workGroupTable.put(wgCounter, new ActivityWorkGroup(wgCounter, duration, priority, cond));
        return wgCounter++;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration) {    	
        return addWorkGroup(duration, 0, new TrueCondition());
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(ModelTimeFunction duration, Condition cond) {    	
        return addWorkGroup(duration, 0, cond);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    @Override
	public ActivityWorkGroup getWorkGroup(int wgId) {
        return (ActivityWorkGroup)super.getWorkGroup(wgId);
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "TACT";
	}
	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
	 * this workgroup, and the priority of the workgroup inside the activity.
	 * @author Iván Castilla Rodríguez
	 */
	public class ActivityWorkGroup extends Activity.ActivityWorkGroup implements TimeDrivenActivityWorkGroup {
	    /** Duration of the activity when using this WG */
	    final protected TimeFunction duration;
		
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, ModelTimeFunction duration, int priority, Condition cond) {
	        super(id, priority, cond);
	        this.duration = duration.getFunction();
	    }

	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, ModelTimeFunction duration, int priority, es.ull.isaatc.simulation.model.WorkGroup wg, Condition cond) {
	        super(id, priority, wg, cond);
	        this.duration = duration.getFunction();
	    }


	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    @Override
		protected TimeDrivenActivity getActivity() {
	        return TimeDrivenActivity.this;
	    }

	    /**
	     * Returns the duration of the activity where this workgroup is used. 
	     * @return The activity duration.
	     */
	    public TimeFunction getDuration() {
	        return duration;
	    }
	    
	    @Override
	    public String toString() {
	    	return new String(super.toString());
	    }

	}
	@Override
	public EnumSet<Modifier> getModifiers() {
		return modifiers;
	}

}
