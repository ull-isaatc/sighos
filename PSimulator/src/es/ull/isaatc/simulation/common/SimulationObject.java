/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iván Castilla Rodríguez
 *
 */
public interface SimulationObject extends Identifiable, Comparable<SimulationObject> {
	Simulation getSimulation();
}
