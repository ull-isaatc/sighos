/**
 * 
 */
package es.ull.iis.simulation.sequential.flow;

import es.ull.iis.simulation.sequential.Simulation;
import es.ull.iis.simulation.sequential.WorkThread;

/**
 * A merge flow which allows all the true incoming branches to pass.
 * @author Iván Castilla Rodríguez
 */
public abstract class ORJoinFlow extends MergeFlow implements es.ull.iis.simulation.core.flow.ORJoinFlow<WorkThread> {

	/**
	 * Creates a new OR Join flow.
	 * @param simul Simulation this flow belongs to
	 */
	public ORJoinFlow(Simulation simul) {
		super(simul);
	}
	
}
