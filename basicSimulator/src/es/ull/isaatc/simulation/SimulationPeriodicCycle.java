/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationPeriodicCycle implements	SimulationCycle {
	private final PeriodicCycle cycle;
	
	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, SimulationTimeFunction period, SimulationTime endTs) {
		cycle = new PeriodicCycle(simul.simulationTime2Double(startTs), period.getFunction(), simul.simulationTime2Double(endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, SimulationTimeFunction period,
			int iterations) {
		cycle = new PeriodicCycle(simul.simulationTime2Double(startTs), period.getFunction(), iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, SimulationTimeFunction period,
			SimulationTime endTs, SimulationCycle subCycle) {
		cycle = new PeriodicCycle(simul.simulationTime2Double(startTs), period.getFunction(), simul.simulationTime2Double(endTs), (Cycle)subCycle);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, SimulationTimeFunction period,
			int iterations, SimulationCycle subCycle) {
		cycle = new PeriodicCycle(simul.simulationTime2Double(startTs), period.getFunction(), iterations, (Cycle)subCycle);
	}

	/**
	 * @return the cycle
	 */
	public Cycle getCycle() {
		return cycle;
	}

}
