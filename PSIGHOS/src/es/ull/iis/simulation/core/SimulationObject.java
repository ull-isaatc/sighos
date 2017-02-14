/**
 * 
 */
package es.ull.iis.simulation.core;


/**
 * An {@link Identifiable} object associated to a {@link Simulation}.
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface SimulationObject extends Identifiable, Comparable<SimulationObject> {
	/**
	 * Returns the associated {@link Simulation}.
	 * @return the associated {@link Simulation}
	 */
	Simulation getSimulation();
}
