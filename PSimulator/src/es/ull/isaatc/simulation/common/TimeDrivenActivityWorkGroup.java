/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.function.TimeFunction;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeDrivenActivityWorkGroup extends ActivityWorkGroup {
	TimeFunction getDuration();
}
