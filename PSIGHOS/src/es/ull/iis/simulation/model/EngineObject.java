/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * An {@link Identifiable} object associated to a {@link SimulationEngine}.
 * @author Iván Castilla Rodríguez
 *
 */
public interface EngineObject extends Identifiable, Comparable<EngineObject> {
	/**
	 * Returns the associated {@link SimulationEngine}.
	 * @return the associated {@link SimulationEngine}
	 */
	SimulationEngine getSimulation();
}
