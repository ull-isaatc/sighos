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
	public SimulationPeriodicCycle(Simulation simul, Time startTs, SimulationTimeFunction period, Time endTs) {
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), period.getFunction(), simul.simulationTime2Long(endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public SimulationPeriodicCycle(Simulation simul, Time startTs, SimulationTimeFunction period,
			int iterations) {
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), period.getFunction(), iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, Time startTs, SimulationTimeFunction period,
			Time endTs, SimulationCycle subCycle) {
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), period.getFunction(), simul.simulationTime2Long(endTs), subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, Time startTs, SimulationTimeFunction period,
			int iterations, SimulationCycle subCycle) {
		cycle = new PeriodicCycle(simul.simulationTime2Long(startTs), period.getFunction(), iterations, subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public SimulationPeriodicCycle(Simulation simul, long startTs, SimulationTimeFunction period, long endTs) {
		this(simul, new Time(simul.getUnit(), startTs), period, new Time(simul.getUnit(), endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public SimulationPeriodicCycle(Simulation simul, long startTs, SimulationTimeFunction period,
			int iterations) {
		this(simul, new Time(simul.getUnit(), startTs), period, iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, long startTs, SimulationTimeFunction period,
			long endTs, SimulationCycle subCycle) {
		this(simul, new Time(simul.getUnit(), startTs), period, new Time(simul.getUnit(), endTs), subCycle);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public SimulationPeriodicCycle(Simulation simul, long startTs, SimulationTimeFunction period,
			int iterations, SimulationCycle subCycle) {
		this(simul, new Time(simul.getUnit(), startTs), period, iterations, subCycle);
	}

	/**
	 * @return the cycle
	 */
	public Cycle getCycle() {
		return cycle;
	}

}
