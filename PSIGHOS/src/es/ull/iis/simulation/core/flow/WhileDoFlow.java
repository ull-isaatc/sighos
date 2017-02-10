package es.ull.iis.simulation.core.flow;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.WorkThread;


/**
 * A {@link StructuredLoopFlow} which resembles a while-do loop. A precondition is
 * checked before executing the internal flow. If the postcondition is <tt>false</tt>,
 * this flow finishes.
 * @author Yeray Callero
 */
public interface WhileDoFlow<WT extends WorkThread<?>> extends StructuredLoopFlow<WT> {
	/** 
	 * Returns the condition which controls the loop operation.
	 * @return The condition which controls the loop operation
	 */
	Condition getCondition();
}

