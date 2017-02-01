/**
 * 
 */
package es.ull.iis.simulation.core;

import java.util.ArrayDeque;
import java.util.EnumSet;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * A task which requires certain amount and type of resources to be performed.
 * @author Iván Castilla Rodríguez
 *
 */
public interface Activity<AWG extends ActivityWorkGroup, WT extends WorkThread, R extends Resource> extends BasicStep<AWG, WT, R> {

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
    TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, WorkGroup wg);
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    TimeDrivenActivityWorkGroup addWorkGroup(TimeFunction duration, int priority, WorkGroup wg, Condition cond);

    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    TimeDrivenActivityWorkGroup addWorkGroup(long duration, int priority, WorkGroup wg);
    
    /**
     * Creates a new workgroup for this activity. 
     * @param duration Duration of the activity when performed with the new workgroup
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    TimeDrivenActivityWorkGroup addWorkGroup(long duration, int priority, WorkGroup wg, Condition cond);

    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    FlowDrivenActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg);
    
    /**
     * Creates a new workgroup for this activity. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param priority Priority of the workgroup
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */    
    FlowDrivenActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, int priority, WorkGroup wg, Condition cond);

    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @return The new workgroup created.
     */
    FlowDrivenActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, WorkGroup wg);    	

    /**
     * Creates a new workgroup for this activity with the highest level of priority. 
     * @param initFlow First step of the flow that have to be performed 
     * @param finalFlow Last step of the flow that have to be performed 
     * @param wg The set of pairs <ResurceType, amount> which will perform the activity
     * @param cond Availability condition
     * @return The new workgroup created.
     */
    FlowDrivenActivityWorkGroup addWorkGroup(InitializerFlow initFlow, FinalizerFlow finalFlow, WorkGroup wg, Condition cond);    	
	
	/**
	 * Catches the resources required to carry out this activity and schedules a new event to finish it. 
	 * @param wThread Work thread requesting this basic step
	 */
	public void carryOut(WT wThread, ArrayDeque<R> solution);


}
