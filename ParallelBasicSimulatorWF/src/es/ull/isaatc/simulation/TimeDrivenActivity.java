package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.util.RandomPermutation;

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
 * @author Iv�n Castilla Rodr�guez
 */
public class TimeDrivenActivity extends Activity {
	/** Indicates special characteristics of this activity */
	public enum Modifier {
	    /** Indicates that this activity is non presential, i.e., an element can perform other activities at
	     * the same time */
		NONPRESENTIAL,
		/** Indicates that the activity can be interrupted in case the required resources end their
		 * availability time */
		INTERRUPTIBLE
	}
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
    public int addWorkGroup(SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, duration, priority, wg));
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
    public int addWorkGroup(SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg, Condition cond) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, duration, priority, wg, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, es.ull.isaatc.simulation.WorkGroup wg) {    	
        return addWorkGroup(duration, 0, wg);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, es.ull.isaatc.simulation.WorkGroup wg, Condition cond) {    	
        return addWorkGroup(duration, 0, wg, cond);
    }

    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, int priority) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, duration, priority));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, int priority, Condition cond) {
    	int wgId = workGroupTable.size();
        workGroupTable.add(new ActivityWorkGroup(wgId, duration, priority, cond));
        return wgId;
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration) {    	
        return addWorkGroup(duration, 0);
    }
    
    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param cond Availability condition
     * @return The new workgroup's identifier.
     */
    public int addWorkGroup(SimulationTimeFunction duration, Condition cond) {    	
        return addWorkGroup(duration, 0, cond);
    }

    /**
     * Searches and returns a workgroup with the specified id.
     * @param wgId The id of the workgroup searched
     * @return A workgroup contained in this activity with the specified id
     */
    public ActivityWorkGroup getWorkGroup(int wgId) {
        return (ActivityWorkGroup)super.getWorkGroup(wgId);
    }
    
    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Activity#isFeasible(es.ull.isaatc.simulation.WorkItem)
     */
    @Override
    protected boolean isFeasible(WorkItem wi) {
    	if (super.isFeasible(wi)) {
            if (!isNonPresential())
            	wi.getElement().setCurrent(wi);
    		return true;
    	}
    	return false;
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "TACT";
	}

	/**
	 * An element is valid to perform a time-driven activity is it's not currently carrying 
	 * out another activity or this activity is non presential.
	 * @param wItem Work item requesting this activity 
	 */
	@Override
	public boolean validElement(WorkItem wItem) {
		return (wItem.getElement().getCurrent() == null || isNonPresential());
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#request(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public void request(WorkItem wItem) {
		Element elem = wItem.getElement();
		simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.REQACT, elem.getTs()));
		if (elem.isDebugEnabled())
			elem.debug("Requests\t" + this + "\t" + description);
		elem.debug("MUTEX\trequesting\t" + this + " (req. act.)");    	
        elem.waitSemaphore();
		elem.debug("MUTEX\tadquired\t" + this + " (req. act.)");    	
		// If the element is not performing a presential activity yet or the
		// activity to be requested is non presential
		if (validElement(wItem)) {
			// There are enough resources to perform the activity
			if (isFeasible(wItem)) {
				elem.debug("MUTEX\treleasing\t" + this + " (req. act.)");    	
		    	elem.signalSemaphore();
				elem.debug("MUTEX\tfreed\t" + this + " (req. act.)");    	
				carryOut(wItem);
			}
			else {
				elem.debug("MUTEX\treleasing\t" + this + " (req. act.)");    	
		    	elem.signalSemaphore();
				elem.debug("MUTEX\tfreed\t" + this + " (req. act.)");    	
				queueAdd(wItem); // The element is introduced in the queue
			}
		} else {
			elem.debug("MUTEX\treleasing\t" + this + " (req. act.)");    	
	    	elem.signalSemaphore();
			elem.debug("MUTEX\tfreed\t" + this + " (req. act.)");    				
			queueAdd(wItem); // The element is introduced in the queue
		}
	}
	
	/**
	 * Updates the element's timestamp, catch the corresponding resources and
	 * produces a <code>FinishFlowEvent</code>.
	 * @param wItem Work item requesting this activity
	 */
	@Override
	public void carryOut(WorkItem wItem) {
		Element elem = wItem.getElement();
		wItem.getFlow().afterStart(elem);
		double auxTs = wItem.getExecutionWG().catchResources(wItem);
		// The first time the activity is carried out (useful only for interruptible activities)
		if (Double.isNaN(wItem.getTimeLeft())) {
			wItem.setTimeLeft(((TimeDrivenActivity.ActivityWorkGroup)wItem.getExecutionWG()).getDuration());
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.STAACT, elem.getTs()));
			elem.debug("Starts\t" + this + "\t" + description);			
		}
		else {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.RESACT, elem.getTs()));
			elem.debug("Continues\t" + this + "\t" + description);						
		}
		double finishTs = elem.getTs() + wItem.getTimeLeft();
		// The required time for finishing the activity is reduced (useful only for interruptible activities)
		if (isInterruptible() && (finishTs - auxTs > 0.0))
			wItem.setTimeLeft(finishTs - auxTs);				
		else {
			auxTs = finishTs;
			wItem.setTimeLeft(0.0);
		}
		elem.addEvent(elem.new FinishFlowEvent(auxTs, wItem.getFlow(), wItem.getWorkThread()));
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Activity#finish(es.ull.isaatc.simulation.WorkItem)
	 */
	@Override
	public boolean finish(WorkItem wItem) {
		Element elem = wItem.getElement();

		ArrayList<ActivityManager> amList = wItem.releaseCaughtResources();
		if (!isNonPresential())
			elem.setCurrent(null);

		int[] order = RandomPermutation.nextPermutation(amList.size());
		for (int ind : order) {
			elem.addAvailableResourceEvent(amList.get(ind));
		}

		// FIXME: CUIDADO CON ESTO!!! Nunca deber�a ser menor
		if (wItem.getTimeLeft() <= 0.0) {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.ENDACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes\t" + this + "\t" + description);
			// Checks if there are pending activities that haven't noticed the
			// element availability
			if (!isNonPresential())
				elem.addAvailableElementEvents();
			return true;
		}
		// Added the condition(Lancaster compatibility), even when it should be unnecessary.
		else {
			simul.getInfoHandler().notifyInfo(new ElementActionInfo(this.simul, wItem, elem, ElementActionInfo.Type.INTACT, elem.getTs()));
			if (elem.isDebugEnabled())
				elem.debug("Finishes part of \t" + this + "\t" + description + "\t" + wItem.getTimeLeft());				
			// The element is introduced in the queue
			queueAdd(wItem); 
		}
		return false;
		
	}
	
	/**
	 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
	 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
	 * this workgroup, and the priority of the workgroup inside the activity.
	 * @author Iv�n Castilla Rodr�guez
	 */
	public class ActivityWorkGroup extends Activity.ActivityWorkGroup {
	    /** Duration of the activity when using this WG */
	    final protected TimeFunction duration;
		
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     */    
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority) {
	        super(id, priority);
	        this.duration = duration.getFunction();
	    }
	    
	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param cond  Availability condition
	     */    
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority, Condition cond) {
	        super(id, priority, cond);
	        this.duration = duration.getFunction();
	    }

	    /**
	     * Creates a new instance of WorkGroup
	     * @param id Identifier of this workgroup.
	     * @param duration Duration of the activity when using this WG.
	     * @param priority Priority of the workgroup.
	     * @param wg Original workgroup
	     */    
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg) {
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
	    protected ActivityWorkGroup(int id, SimulationTimeFunction duration, int priority, es.ull.isaatc.simulation.WorkGroup wg, Condition cond) {
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
	     * In this case, it returns 0.0.
	     * @return The activity duration.
	     */
	    public double getDuration() {
	        return duration.getPositiveValue(getTs());
	    }
	    
	    @Override
	    public String toString() {
	    	return new String(super.toString());
	    }

	}

}