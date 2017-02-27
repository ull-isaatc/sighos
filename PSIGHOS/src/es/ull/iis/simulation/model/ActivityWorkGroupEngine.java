/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.model.flow.ActivityFlow;

/**
 * A {@link WorkGroup} which is used inside an {@link ActivityFlow}. It may have an associated 
 * {@link Condition} and a priority.
 * @author Iván Castilla Rodríguez
 */
public interface ActivityWorkGroupEngine {
	boolean isFeasible(FlowExecutor fe);
}
