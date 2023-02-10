/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import es.ull.iis.simulation.model.Element;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.port.parking.VesselCreator.VesselGenerationInfo;

/**
 * @author Iván Castilla
 *
 */
public class Vessel extends Element {
	public static final int SIZE = 1;

	/**
	 * @param simul
	 * @param elementType
	 * @param initialFlow
	 */
	public Vessel(Simulation simul, VesselGenerationInfo info) {
		super(simul, info, "VESSEL");
	}

}
