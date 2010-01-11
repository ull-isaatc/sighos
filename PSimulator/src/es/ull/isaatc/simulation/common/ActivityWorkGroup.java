/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.util.Prioritizable;

/**
 * A {@link WorkGroup} which may have an associated {@link Condition}
 * @author Iván Castilla Rodríguez
 */
public interface ActivityWorkGroup extends WorkGroup, Prioritizable, Describable, Identifiable {
	/**
	 * Returns the associated {@link Condition} to this {@link Workgroup}.
	 * @return the associated {@link Condition} to this {@link Workgroup}
	 */
	Condition getCondition();
}
