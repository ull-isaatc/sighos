/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

/**
 * @author Iván Castilla
 *
 */
public class PatientInfoView extends StdInfoView {

	/**
	 * @param simul
	 */
	public PatientInfoView(Simulation simul) {
		super(simul);
		addEntrance(PatientInfo.class);
	}

}
