package es.ull.isaatc.simulation.xoptGroupedThreaded.flow;

import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.xoptGroupedThreaded.Simulation;
import es.ull.isaatc.simulation.xoptGroupedThreaded.WorkThread;

/**
 * A structured loop flow which resembles a do-while loop. The internal flow
 * is executed the first time and then the postcondition is checked. If the
 * postcondition is true, the internal flow is executed again; if not, this
 * flow finishes. 
 * @author ycallero
 */
public class DoWhileFlow extends StructuredLoopFlow implements es.ull.isaatc.simulation.common.flow.DoWhileFlow {
	/** Condition which controls the loop operation. */
	protected final Condition cond;
	
	/**
	 * Create a new DoWhileFlow.
	 * @param simul Simulation this flow belongs to
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public DoWhileFlow(Simulation simul, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, Condition postCondition) {
		super(simul, initialSubFlow, finalSubFlow);
		cond = postCondition;
	}

	/**
	 * Create a new DoWhileFlow.
	 * @param simul Simulation this flow belongs to
	 * @param subFlow A unique flow defining an internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public DoWhileFlow(Simulation simul, TaskFlow subFlow, Condition postCondition) {
		this(simul, subFlow, subFlow, postCondition);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement())) {
					// Continue the simulation to the loop MetaFlow.
					initialFlow.request(wThread.getInstanceDescendantWorkThread(initialFlow));
				} 
				else {
					wThread.setExecutable(false, this);
					next(wThread);				
				}
			}
			else {
				wThread.updatePath(this);
				next(wThread);
			}
		} else
			wThread.notifyEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.TaskFlow#finish(es.ull.isaatc.simulation.WorkThread)
	 */
	public void finish(WorkThread wThread) {
		if (cond.check(wThread.getElement())) {
			initialFlow.request(wThread.getInstanceDescendantWorkThread(initialFlow));
		} else {
			afterFinalize(wThread.getElement());
			next(wThread);
		}
	}

	@Override
	public Condition getCondition() {
		return cond;
	}
	
}
