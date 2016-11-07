package es.ull.iis.simulation.parallel;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;

/**
 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, the duration of the activity when using 
 * this workgroup, and the priority of the workgroup inside the activity.
 * @author Iván Castilla Rodríguez
 */
public class TimeDrivenActivityWorkGroup extends ActivityWorkGroup implements es.ull.iis.simulation.core.TimeDrivenActivityWorkGroup {
    /** Duration of the activity when using this WG */
    final protected TimeFunction duration;
	
    /**
     * Creates a new instance of WorkGroup
     * @param timeDrivenActivity Related activity
     * @param id Identifier of this workgroup.
     * @param duration Duration of the activity when using this WG.
     * @param priority Priority of the workgroup.
     * @param wg Original workgroup
     */    
    protected TimeDrivenActivityWorkGroup(Activity timeDrivenActivity, int id, TimeFunction duration, int priority, WorkGroup wg) {
        super(timeDrivenActivity, id, priority, wg);
        this.duration = duration;
    }
    
    /**
     * Creates a new instance of WorkGroup
     * @param timeDrivenActivity Related activity
     * @param id Identifier of this workgroup.
     * @param duration Duration of the activity when using this WG.
     * @param priority Priority of the workgroup.
     * @param cond  Availability condition
     */    
    protected TimeDrivenActivityWorkGroup(Activity timeDrivenActivity, int id, TimeFunction duration, int priority, WorkGroup wg, Condition cond) {
        super(timeDrivenActivity, id, priority, wg, cond);
        this.duration = duration;
    }


    /**
     * Returns the duration of the activity where this workgroup is used. 
     * The value returned by the random number function could be negative. 
     * In this case, it returns 0.
     * @return The activity duration.
     */
    public long getDurationSample(Element elem) {
        return Math.round(duration.getValue(elem));
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
}