/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface TimeDrivenActivityWorkGroup extends ActivityWorkGroup {
	TimeFunction getDuration();
}
