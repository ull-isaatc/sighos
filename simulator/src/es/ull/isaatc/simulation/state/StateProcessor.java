/**
 * 
 */
package es.ull.isaatc.simulation.state;

/**
 * Represents an object capable of processing the state of a simulation.
 * @author Iván Castilla Rodríguez
 *
 */
public interface StateProcessor {
	/**
	 * takes the state of a simulation and processes it.
	 * @param state The state of a simulation.
	 */
	void process(SimulationState state);
}
