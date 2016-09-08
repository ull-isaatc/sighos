/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.function.TimeFunction;

/**
 * An {@link ActivityWorkGroup} which includes a duration when associated to a {@link TimeDrivenActivity}.
 * @author Iv�n Castilla Rodr�guez
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