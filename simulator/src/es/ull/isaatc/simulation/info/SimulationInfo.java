/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.EventObject;

/**
 * An information related to any component of a simulation (including the simulation itself).
 * This information is "emitted" by a simulation object and "recovered" by a listener, which
 * processes this information. 
 * @author Iván Castilla Rodríguez
 */
public abstract class SimulationInfo extends EventObject {

	public SimulationInfo(Object source) {
		super(source);
	}

}
