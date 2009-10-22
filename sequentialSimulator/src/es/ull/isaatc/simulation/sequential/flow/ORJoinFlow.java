/**
 * 
 */
package es.ull.isaatc.simulation.sequential.flow;

import es.ull.isaatc.simulation.sequential.Simulation;

/**
 * A merge flow which allows all the true incoming branches to pass.
 * @author Iván Castilla Rodríguez
 */
public abstract class ORJoinFlow extends MergeFlow {

	/**
	 * Creates a new OR Join flow.
	 * @param simul Simulation this flow belongs to
	 */
	public ORJoinFlow(Simulation simul) {
		super(simul);
	}
	
}
