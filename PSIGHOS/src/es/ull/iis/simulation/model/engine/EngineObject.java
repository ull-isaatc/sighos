/**
 * 
 */
package es.ull.iis.simulation.model.engine;

import es.ull.iis.simulation.model.Identifiable;

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
	SimulationEngine getSimulationEngine();
}
