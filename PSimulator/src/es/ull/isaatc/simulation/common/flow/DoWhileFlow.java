package es.ull.isaatc.simulation.common.flow;

import es.ull.isaatc.simulation.common.condition.Condition;

/**
 * A {@link StructuredLoopFlow} which resembles a do-while loop. The internal flow
 * is executed the first time and then the postcondition is checked. If the
 * postcondition is <tt>true</tt>, the internal flow is executed again; if not, this
 * flow finishes. 
 * @author Yeray Callero
 */
public interface DoWhileFlow extends StructuredLoopFlow {
	/** 
	 * Returns the condition which controls the loop operation.
	 * @return The condition which controls the loop operation
	 */
	Condition getCondition();
}
