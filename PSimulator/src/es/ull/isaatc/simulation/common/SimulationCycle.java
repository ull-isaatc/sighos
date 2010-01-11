/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Cycle;

/**
 * A wrapper class for {@link es.ull.isaatc.util.Cycle Cycle} to be used inside a simulation. 
 * Thus {@link TimeStamp} can be used to define the cycle parameters.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationCycle {
	/**
	 * Returns the inner {@link es.ull.isaatc.util.Cycle Cycle}.
	 * @return the inner {@link es.ull.isaatc.util.Cycle Cycle}
	 */
	Cycle getCycle();
}
