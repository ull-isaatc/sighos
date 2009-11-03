package es.ull.isaatc.simulation.model.flow;

import es.ull.isaatc.simulation.model.Model;
import es.ull.isaatc.simulation.model.condition.Condition;

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
	 * @param model Model this flow belongs to.
	 * @param initialSubFlow First step of the internal subflow
	 * @param finalSubFlow Last step of the internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public DoWhileFlow(Model model, InitializerFlow initialSubFlow, FinalizerFlow finalSubFlow, Condition postCondition) {
		super(model, initialSubFlow, finalSubFlow);
		cond = postCondition;
	}

	/**
	 * Create a new DoWhileFlow.
	 * @param model Model this flow belongs to.
	 * @param subFlow A unique flow defining an internal subflow
	 * @param postCondition Break loop condition.
 	 */
	public DoWhileFlow(Model model, TaskFlow subFlow, Condition postCondition) {
		this(model, subFlow, subFlow, postCondition);
	}

	@Override
	public Condition getCondition() {
		return cond;
	}

}
