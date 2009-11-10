package es.ull.isaatc.simulation.optThreaded.flow;

import es.ull.isaatc.simulation.optThreaded.Simulation;
import es.ull.isaatc.simulation.optThreaded.WorkThread;
import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * A structured loop flow which resembles a while-do loop. A precondition is
 * checked before executing the internal flow. If the postcondition is false,
 * this flow finishes. 
 * @author ycallero
 */
public class WhileDoFlow extends StructuredLoopFlow implements es.ull.isaatc.simulation.common.flow.WhileDoFlow {
	/** Condition which controls the loop operation. */
	protected final Condition cond;
	
	/**
	 * Create a new WhileDoFlow.
	 * @param simul Simulation this flow belongs to
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param prevCondition Break loop condition.
 	 */
	public WhileDoFlow(Simulation simul, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, Condition prevCondition) {
		super(simul, initialSubFlow, finalSubFlow);
		cond = prevCondition;
	}

	/**
	 * Create a new WhileDoFlow.
	 * @param simul Simulation this flow belongs to
	 * @param subFlow A unique flow defining an internal subflow
	 * @param prevCondition Break loop condition.
 	 */
	public WhileDoFlow(Simulation simul, TaskFlow subFlow, Condition prevCondition) {
		this(simul, subFlow, subFlow, prevCondition);
	}

	/*
	 * (non-Javadoc)
	 * @see es.ull.isaatc.simulation.Flow#request(es.ull.isaatc.simulation.WorkThread)
	 */
	public void request(WorkThread wThread) {
		if (!wThread.wasVisited(this)) {
			if (wThread.isExecutable()) {
				if (beforeRequest(wThread.getElement())) {
					finish(wThread);
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
		// The loop condition is checked
		if (cond.check(wThread.getElement())) {
			wThread.getElement().addRequestEvent(initialFlow, wThread.getInstanceDescendantWorkThread(initialFlow));
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

