/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.simulation.Identifiable;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObject extends Identifiable {
	Simulation getSimulation();
}
