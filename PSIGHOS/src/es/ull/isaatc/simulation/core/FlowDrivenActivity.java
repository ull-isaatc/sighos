/**
 * 
 */
package es.ull.isaatc.simulation.core;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.core.flow.FinalizerFlow;
import es.ull.isaatc.simulation.core.flow.InitializerFlow;

/**
 * An {@link Activity} which could be carried out by an {@link Element} and whose duration depends 
 * on the finalization of an internal {@link es.ull.isaatc.simulation.core.flow.Flow Flow}. 
 * @author Iván Castilla Rodríguez
 */
public interface FlowDrivenActivity extends Activity {

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
	
}
