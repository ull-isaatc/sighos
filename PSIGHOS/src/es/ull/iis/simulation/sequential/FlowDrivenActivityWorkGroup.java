package es.ull.iis.simulation.sequential;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.sequential.flow.ActivityFlow;
import es.ull.iis.simulation.core.flow.FinalizerFlow;
import es.ull.iis.simulation.core.flow.InitializerFlow;

/**
 * A set of resources needed for carrying out an activity. A workgroup (WG) consists on a 
 * set of (resource type, #needed resources) pairs, a subflow, and the priority of the 
 * workgroup inside the activity.
 * @author Iván Castilla Rodríguez
 */
public class FlowDrivenActivityWorkGroup extends es.ull.iis.simulation.sequential.ActivityWorkGroup implements es.ull.iis.simulation.core.FlowDrivenActivityWorkGroup<WorkThread> {
	/** The first step of the subflow */
    final protected InitializerFlow<WorkThread> initialFlow;
    /** The last step of the subflow */
    final protected FinalizerFlow<WorkThread> finalFlow;
    
    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param initialFlow The first step of the flow 
     * @param finalFlow The last step of the flow
     * @param priority Priority of the workgroup.
     * @param wg Original workgroup
     * @param flowDrivenActivity TODO
     */    
    public FlowDrivenActivityWorkGroup(ActivityFlow flowDrivenActivity, int id, InitializerFlow<WorkThread> initialFlow, 
    		FinalizerFlow<WorkThread> finalFlow, int priority, WorkGroup wg) {
        super(flowDrivenActivity, id, priority, wg);
        this.initialFlow = initialFlow;
        this.finalFlow = finalFlow;
    }
    
    /**
     * Creates a new instance of WorkGroup
     * @param id Identifier of this workgroup.
     * @param initialFlow Initial Flow
     * @param finalFlow Final Flow
     * @param priority Priority of the workgroup.
     * @param wg WorkGroup
     * @param cond  Availability condition
     * @param flowDrivenActivity TODO
     */    
    public FlowDrivenActivityWorkGroup(ActivityFlow flowDrivenActivity, int id, InitializerFlow<WorkThread> initialFlow, 
    		FinalizerFlow<WorkThread> finalFlow, int priority, WorkGroup wg, Condition cond) {
        super(flowDrivenActivity, id, priority, wg, cond);
        this.initialFlow = initialFlow;
        this.finalFlow = finalFlow;
    }

    /**
     * Returns the first step of the subflow
	 * @return the initialFlow
	 */
	public InitializerFlow<WorkThread> getInitialFlow() {
		return initialFlow;
	}

	/**
     * Returns the last step of the subflow
	 * @return the finalFlow
	 */
	public FinalizerFlow<WorkThread> getFinalFlow() {
		return finalFlow;
	}

}