/**
 * 
 */
package es.ull.iis.simulation.model.flow;

import es.ull.iis.simulation.model.Simulation;

/**
 * A merge flow which allows all the true incoming branches to pass.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class ORJoinFlow extends MergeFlow {

	/**
	 * Creates a new OR Join flow.
	 */
	public ORJoinFlow(Simulation model) {
		super(model);
	}
	
}
