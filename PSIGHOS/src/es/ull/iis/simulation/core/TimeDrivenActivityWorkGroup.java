/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.model.ActivityWorkGroupEngine;

/**
 * An {@link ActivityWorkGroupEngine} which includes a duration when associated to a {@link TimeDrivenActivity}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface TimeDrivenActivityWorkGroup extends ActivityWorkGroupEngine {
	/**
	 * Returns the {@link TimeFunction} which defines the duration of performing an activity
	 * by using this workgroup.
	 * @return The {@link TimeFunction} which defines the duration of performing an activity
	 * by using this workgroup
	 */
	TimeFunction getDuration();
}
