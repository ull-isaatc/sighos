package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.WorkThread;

/**
 * A {@link StructuredLoopFlow} which resembles a do-while loop. The internal flow
 * is executed the first time and then the postcondition is checked. If the
 * postcondition is <tt>true</tt>, the internal flow is executed again; if not, this
 * flow finishes. 
 * @author Yeray Callero
 */
public interface DoWhileFlow<WT extends WorkThread<?>> extends StructuredLoopFlow<WT> {
	/** 
	 * Returns the condition which controls the loop operation.
	 * @return The condition which controls the loop operation
	 */
	Condition getCondition();
}
