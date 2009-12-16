/**
 * 
 */
package es.ull.isaatc.simulation.groupedThreaded.flow;

import es.ull.isaatc.simulation.groupedThreaded.Simulation;

/**
 * A merge flow which allows all the true incoming branches to pass.
 * @author Iván Castilla Rodríguez
 */
public abstract class ORJoinFlow extends MergeFlow implements es.ull.isaatc.simulation.common.flow.ORJoinFlow {

	/**
	 * Creates a new OR Join flow.
	 * @param simul Simulation this flow belongs to
	 */
	public ORJoinFlow(Simulation simul) {
		super(simul);
	}
	
}
