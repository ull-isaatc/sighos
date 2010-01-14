package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.condition.Condition;


/**
 * A {@link StructuredLoopFlow} which resembles a while-do loop. A precondition is
 * checked before executing the internal flow. If the postcondition is <tt>false</tt>,
 * this flow finishes.
 * @author Yeray Callero
 */
public interface WhileDoFlow extends StructuredLoopFlow {
	/** 
	 * Returns the condition which controls the loop operation.
	 * @return The condition which controls the loop operation
	 */
	Condition getCondition();
}

