/**
 * 
 */
package es.ull.isaatc.test;

import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.listener.SimulationListener;

/**
 * A verification listener which tests:<ul>
 * <li> the initial simulation timestamp</li>
 * <li> the final simulation timestamp</li>
 * <li> causality constraint</li>
 * Any error is printed on the screen.
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationGeneralTestListener implements SimulationListener {
	private double ts = 0.0;
	private double startTs;
	private double endTs;
	private boolean resultStart = true;
	private boolean resultEnd = true;
	private boolean resultTime = true;

	/**
	 * @param startTs
	 * @param endTs
	 */
	public SimulationGeneralTestListener(double startTs, double endTs) {
		this.startTs = startTs;
		this.endTs = endTs;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationObjectInfo)
	 */
	public void infoEmited(SimulationObjectInfo info) {
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationStartInfo)
	 */
	public void infoEmited(SimulationStartInfo info) {
		if (info.getSimulation().getStartTs() != startTs)
			resultStart = false;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.SimulationEndInfo)
	 */
	public void infoEmited(SimulationEndInfo info) {
		if (info.getSimulation().getEndTs() != endTs)
			resultEnd = false;
		System.out.println("General Simulation Test: ");
		System.out.println("\tInitial timestamp:\t" + (resultStart? "OK" : "ERROR"));
		System.out.println("\tFinal timestamp:\t" + (resultEnd? "OK" : "ERROR"));
		System.out.println("\tCausality constraint:\t" + (resultTime? "OK" : "ERROR"));
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.info.SimulationListener#infoEmited(es.ull.isaatc.simulation.info.TimeChangeInfo)
	 */
	public void infoEmited(TimeChangeInfo info) {
		if (info.getTs() < ts)
			resultTime = false;
	}

}
