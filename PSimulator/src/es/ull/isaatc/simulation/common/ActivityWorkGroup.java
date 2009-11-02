/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Describable;
import es.ull.isaatc.simulation.Identifiable;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.util.Prioritizable;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface ActivityWorkGroup extends WorkGroup, Prioritizable, Describable, Identifiable {
	Condition getCondition();
}
