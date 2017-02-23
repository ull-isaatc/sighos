/**
 * 
 */
package es.ull.iis.simulation.core;

import es.ull.iis.simulation.model.Identifiable;
import es.ull.iis.simulation.model.SimulationEngine;

/**
 * An {@link Identifiable} object associated to a {@link SimulationEngine}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObject extends Identifiable, Comparable<SimulationObject> {
	/**
	 * Returns the associated {@link SimulationEngine}.
	 * @return the associated {@link SimulationEngine}
	 */
	SimulationEngine getSimulation();
}
