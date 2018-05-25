/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.util.cycle.Cycle;

/**
 * A wrapper class for {@link es.ull.iis.util.cycle.Cycle Cycle} to be used inside a simulation. 
 * Thus {@link TimeStamp} can be used to define the cycle parameters.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface SimulationCycle {
	/**
	 * Returns the inner {@link es.ull.iis.util.cycle.Cycle Cycle}.
	 * @return the inner {@link es.ull.iis.util.cycle.Cycle Cycle}
	 */
	Cycle getCycle();
}
