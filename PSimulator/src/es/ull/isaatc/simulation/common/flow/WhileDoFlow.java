package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.condition.Condition;


/**
 * A structured loop flow which resembles a while-do loop. A precondition is
 * checked before executing the internal flow. If the postcondition is false,
 * this flow finishes. 
 * @author ycallero
 */
public interface WhileDoFlow extends StructuredLoopFlow {
	/** Condition which controls the loop operation. */
	Condition getCondition();
}

