package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.Model;

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
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public WhileDoFlow(Model model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, Condition postCondition) {
		super(model, initialSubFlow, finalSubFlow);
		cond = postCondition;
	}

	/**
	 * Create a new WhileDoFlow.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public WhileDoFlow(Model model, TaskFlow subFlow, Condition postCondition) {
		this(model, subFlow, subFlow, postCondition);
	}

	/** 
	 * Returns the condition which controls the loop operation.
	 * @return The condition which controls the loop operation
	 */
	public Condition getCondition() {
		return cond;
	}

}

