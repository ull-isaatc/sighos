/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.util.Cycle;

/**
 * A wrapper class for {@link es.ull.iis.util.Cycle Cycle} to be used inside a simulation. 
 * Thus {@link TimeStamp} can be used to define the cycle parameters.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationCycle {
	/**
	 * Returns the inner {@link es.ull.iis.util.Cycle Cycle}.
	 * @return the inner {@link es.ull.iis.util.Cycle Cycle}
	 */
	Cycle getCycle();
}
