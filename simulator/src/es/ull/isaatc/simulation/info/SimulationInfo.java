/**
 * 
 */
package es.ull.isaatc.simulation.info;

import java.util.EventObject;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public abstract class SimulationInfo extends EventObject {
	/**	 */
	private static final long serialVersionUID = -7590046311770021342L;

	public SimulationInfo(Object source) {
		super(source);
	}

}
