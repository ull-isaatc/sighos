package es.ull.isaatc.simulation.flow;

import es.ull.isaatc.simulation.condition.Condition;
import es.ull.isaatc.simulation.model.Model;

/**
 * A structured loop flow which resembles a while-do loop. A precondition is
 * checked before executing the internal flow. If the postcondition is false,
 * this flow finishes. 
 * @author ycallero
 */
public class WhileDoFlow extends StructuredLoopFlow {
	/** Condition which controls the loop operation. */
	protected final Condition cond;
	
	/**
	 * Create a new WhileDoFlow.
	 * @param model Model this flow belongs to.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param prevCondition Break loop condition.
 	 */
	public WhileDoFlow(Model model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, Condition prevCondition) {
		super(model, initialSubFlow, finalSubFlow);
		cond = prevCondition;
	}

	/**
	 * Create a new WhileDoFlow.
	 * @param model Model this flow belongs to.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param prevCondition Break loop condition.
 	 */
	public WhileDoFlow(Model model, TaskFlow subFlow, Condition prevCondition) {
		this(model, subFlow, subFlow, prevCondition);
	}
}

