/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.function.TimeFunction;

/**
 * An {@link AntivityWorkGroup} which includes a duration when associated to a {@link TimeDrivenActivity}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeDrivenActivityWorkGroup extends ActivityWorkGroup {
	/**
	 * Returns the {@link TimeFunction} which defines the duration of performing an activity
	 * by using this workgroup.
	 * @return The {@link TimeFunction} which defines the duration of performing an activity
	 * by using this workgroup
	 */
	TimeFunction getDuration();
}
