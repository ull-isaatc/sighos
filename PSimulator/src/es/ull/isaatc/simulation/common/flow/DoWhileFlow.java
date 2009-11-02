package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * A structured loop flow which resembles a do-while loop. The internal flow
 * is executed the first time and then the postcondition is checked. If the
 * postcondition is true, the internal flow is executed again; if not, this
 * flow finishes. 
 * @author ycallero
 */
public interface DoWhileFlow extends StructuredLoopFlow {
	/** Condition which controls the loop operation. */
	Condition getCondition();
}
