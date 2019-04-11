/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public abstract class CheckerListener extends Listener {
	private final StringBuilder problems;

	/**
	 * @param description
	 */
	public CheckerListener(String description) {
		super(description);
		this.problems = new StringBuilder();
		addEntrance(SimulationStartStopInfo.class);
	}

	public boolean testPassed() {
		return problems.length() == 0;
	}

	public String testProblems() {
		return problems.toString();
	}

	protected void addProblem(String id, long ts, String msg) {
		problems.append(id + "\t" + ts + "\t" + msg + System.lineSeparator());
	}
	
}
