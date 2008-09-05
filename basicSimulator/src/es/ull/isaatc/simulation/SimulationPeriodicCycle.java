/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationPeriodicCycle extends PeriodicCycle implements
		SimulationCycle {

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, TimeFunction period, SimulationTime endTs) {
		super(simul.simulationTime2Double(startTs), period, simul.simulationTime2Double(endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, TimeFunction period,
			int iterations) {
		super(simul.simulationTime2Double(startTs), period, iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, TimeFunction period,
			SimulationTime endTs, SimulationCycle subCycle) {
		super(simul.simulationTime2Double(startTs), period, simul.simulationTime2Double(endTs), (Cycle)subCycle);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, SimulationTime startTs, TimeFunction period,
			int iterations, SimulationCycle subCycle) {
		super(simul.simulationTime2Double(startTs), period, iterations, (Cycle)subCycle);
	}

}
