package es.ull.isaatc.simulation.groupedExtra3Phase;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivityWorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;

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
 * TODO Comment
 * @author Iván Castilla Rodríguez
 */
public class TimeDrivenActivity extends Activity implements es.ull.isaatc.simulation.common.TimeDrivenActivity {
	/** The set of modifiers of this activity. */
    protected final EnumSet<Modifier> modifiers;

	/**
     * Creates a new activity with the highest priority and default behavior.
     * @param id Activity's identifier
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     */
    public TimeDrivenActivity(int id, Simulation simul, String description) {
        this(id, simul, description, 0, EnumSet.noneOf(Modifier.class));
    }

    /**
     * Creates a new activity with the specified priority and default behavior.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     */
    public TimeDrivenActivity(int id, Simulation simul, String description, int priority) {
        this(id, simul, description, priority, EnumSet.noneOf(Modifier.class));
    }
    
    /**
     * Creates a new activity with the highest priority and customized behavior.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public TimeDrivenActivity(int id, Simulation simul, String description, EnumSet<Modifier> modifiers) {
        this(id, simul, description, 0, modifiers);
    }

    /**
     * Creates a new activity with the specified priority and customized behavior.
     * @param id Activity's identifier.
     * @param simul Simulation which this activity is attached to.
     * @param description A short text describing this Activity.
     * @param priority Activity's priority.
     * @param modifiers Indicates if the activity has special characteristics. 
     */
    public TimeDrivenActivity(int id, Simulation simul, String description, int priority, EnumSet<Modifier> modifiers) {
        super(id, simul, description, priority);
        this.modifiers = modifiers;
    }

	@Override
	public EnumSet<Modifier> getModifiers() {
		return modifiers;
	}
	
	@Override
    public boolean isNonPresential() {
        return modifiers.contains(Modifier.NONPRESENTIAL);
    }
    
	@Override
	public boolean isInterruptible() {
		return modifiers.contains(Modifier.INTERRUPTIBLE);
	}

	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.common.WorkGroup wg) {
    	final ActivityWorkGroup aWg = new ActivityWorkGroup(workGroupTable.size(), duration, priority, (WorkGroup)wg);
        workGroupTable.add(aWg);
        return aWg;
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.common.WorkGroup wg, Condition cond) {
    	final ActivityWorkGroup aWg = new ActivityWorkGroup(workGroupTable.size(), duration, priority, (WorkGroup)wg, cond); 
        workGroupTable.add(aWg);
        return aWg;
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, es.ull.isaatc.simulation.common.WorkGroup wg) {    	
        return addWorkGroup(duration, 0, wg);
    }
    
	@Override
    public TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, es.ull.isaatc.simulation.common.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(duration, 0, wg, cond);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        return (ActivityWorkGroup)super.getWorkGroup(wgId);
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "TACT";
	}

	/**
	 * An element is valid to perform a time-driven activity is it's not currently carrying 
	 * out another activity or this activity is non presential.
	 */
	@Override
	public boolean mainElementActivity() {
		return !isNonPresential();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#request(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public void request(WorkItem wItem) {
		final Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);

		queueAdd(wItem); // The element is introduced in the queue
		manager.notifyElement(wItem);
	}
	
	/**
	 * Updates the element's timestamp, catch the corresponding resources and
	 * produces a <code>FinishFlowEvent</code>.
	 * @param wItem Work item requesting this activity
	 */
	@Override
	public void carryOut(WorkItem wItem) {
		final Element elem = wItem.getElement();
		wItem.getFlow().afterStart(elem);
		long auxTs = wItem.catchResources();
		
		// The first time the activity is carried out (useful only for interruptible activities)
		if (wItem.getTimeLeft() == -1) {
			wItem.setTimeLeft(((TimeDrivenActivity.ActivityWorkGroup)wItem.getExecutionWG()).getDurationSample());
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);			
		}
		else {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.RESACT, elem.getTs()));
			elem.debug("Continues\t" + this + "\t" + description);						
		}
		final long finishTs = elem.getTs() + wItem.getTimeLeft();
		// The required time for finishing the activity is reduced (useful only for interruptible activities)
		if (isInterruptible() && (finishTs - auxTs > 0))
			wItem.setTimeLeft(finishTs - auxTs);				
		else {
			auxTs = finishTs;
			wItem.setTimeLeft(0);
		}
		elem.addEvent(elem.new FinishFlowEvent(auxTs, wItem.getFlow(), wItem.getWorkThread()));
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#finish(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public boolean finish(WorkItem wItem) {
		final Element elem = wItem.getElement();

		wItem.releaseCaughtResources();
		if (!isNonPresential())
			elem.setCurrent(null);

		assert wItem.getTimeLeft() >= 0 : "Time left < 0: " + wItem.getTimeLeft();
		if (wItem.getTimeLeft() == 0) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
			// Checks if there are pending activities that haven't noticed the
			// element availability
			if (!isNonPresential())
				elem.addAvailableElementEvents();
			return true;
		}
		else {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes part of \t" + this + "\t" + description + "\t" + wItem.getTimeLeft());				
			// The element is introduced in the queue
			queueAdd(wItem); 
			// FIXME: ¿No debería hacer un availableElements también?
		}
		return false;
		
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
	     * @param wg Original workgroup
	     */    
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority, WorkGroup wg) {
	        super(id, priority, wg);
	        this.duration = duration.getFunction();
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority, WorkGroup wg, Condition cond) {
	        super(id, priority, wg, cond);
	        this.duration = duration.getFunction();
	    }


	    /**
	     * Returns the activity this WG belongs to.
	     * @return Activity this WG belongs to.
	     */    
	    protected TimeDrivenActivity getActivity() {
	        return TimeDrivenActivity.this;
	    }

	    /**
	     * Returns the duration of the activity where this workgroup is used. 
	     * The value returned by the random number function could be negative. 
	     * In this case, it returns 0.
	     * @return The activity duration.
	     */
	    public long getDurationSample() {
	        return Math.round(duration.getPositiveValue(getTs()));
	    }
	    
	    /**
	     * Returns the duration of the activity where this workgroup is used. 
	     * The value returned by the random number function could be negative. 
	     * In this case, it returns 0.
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

}
