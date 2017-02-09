/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.core.flow.ActivityFlow;
import es.ull.iis.util.Prioritizable;

/**
 * A {@link WorkGroup} which is used inside an {@link ActivityFlow}. It may have an associated 
 * {@link Condition} and a priority.
 * @author Iván Castilla Rodríguez
 */
public interface ActivityWorkGroup extends WorkGroup, Prioritizable, Describable, Identifiable {
	/**
	 * Returns the associated {@link Condition} to this {@link WorkGroup}.
	 * @return the associated {@link Condition} to this {@link WorkGroup}
	 */
	Condition getCondition();
}
