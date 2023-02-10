/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.port.parking.TruckCreator.TruckGenerationInfo;

/**
 * @author Iván Castilla
 *
 */
public class Truck extends Element {
	public static final int SIZE = 1;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Truck(Simulation simul, TruckGenerationInfo info) {
		super(simul, info, "TRUCK");
	}

}
