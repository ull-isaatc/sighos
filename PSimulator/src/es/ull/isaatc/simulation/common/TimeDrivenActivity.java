/**
 * 
 */
package es.ull.isaatc.simulation.common;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * An {@link Activity} whose finalization is driven by a timestamp, i.e. the activity starts when
 * there are enough resources and finishes after a period of time.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeDrivenActivity extends Activity {
	/** Indicates special characteristics of this activity */
	enum Modifier {
	    /** Indicates that this activity is non presential, i.e., an element can perform other activities at
	     * the same time */
		NONPRESENTIAL,
		/** Indicates that the activity can be interrupted in case the required resources end their
		 * availability time */
		INTERRUPTIBLE
	}
	
	/** 
	 * Returns <tt>true</tt> if the activity is non presential, i.e., an element can perform other 
	 * activities at the same time. 
	 * @return <tt>True</tt> if the activity is non presential, <tt>false</tt> in other case.
	 */
	boolean isNonPresential();
	
	/**
	 * Returns <tt>true</tt> if the activity can be interrupted in case the required resources 
	 * end their availability time. 
	 * @return <tt>True</tt> if the activity can be interrupted, <tt>false</tt> if it keeps the 
	 * resources even if they become not available. 
	 */
	boolean isInterruptible();
	
	/**
	 * Returns the set of modifiers assigned to this activity.
	 * @return The set of modifiers assigned to this activity
	 */
	EnumSet<Modifier> getModifiers();

    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, int priority, WorkGroup wg);
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, int priority, WorkGroup wg, Condition cond);

    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, WorkGroup wg);    	

    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */
    TimeDrivenActivityWorkGroup addWorkGroup(SimulationTimeFunction duration, WorkGroup wg, Condition cond);    	
}
