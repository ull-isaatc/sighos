/**
 * 
 */
package es.ull.isaatc.simulation.common;


/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public interface SimulationObject extends Identifiable, Comparable<SimulationObject> {
	Simulation getSimulation();
}
