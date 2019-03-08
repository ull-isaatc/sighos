/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.info.SimulationTimeInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public abstract class CheckerListener extends Listener {

	/**
	 * @param description
	 */
	public CheckerListener(String description) {
		super(description);
		addEntrance(SimulationTimeInfo.class);
	}

	public abstract boolean testPassed();
	public abstract String testProblems();
}
