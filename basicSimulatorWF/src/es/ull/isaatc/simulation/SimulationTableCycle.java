/**
 * 
 */
package es.ull.isaatc.simulation;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.TableCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationTableCycle implements SimulationCycle {
	private final TableCycle cycle;
	
	/**
	 * @param timestamps
	 */
	public SimulationTableCycle(Simulation simul, SimulationTime[] timestamps) {
		cycle = new TableCycle(simulationTime2Double(simul, timestamps));
	}

	/**
	 * @param timestamps
	 * @param subCycle
	 */
	public SimulationTableCycle(Simulation simul, SimulationTime[] timestamps, SimulationCycle subCycle) {
		cycle = new TableCycle(simulationTime2Double(simul, timestamps), (Cycle)subCycle);
	}

	private static double[] simulationTime2Double(Simulation simul, SimulationTime[] timestamps) {
		double[] times = new double[timestamps.length];
		for (int i = 0; i < times.length; i++)
			times[i] = simul.simulationTime2Double(timestamps[i]);
		return times;
	}

	public Cycle getCycle() {
		return cycle;
	}
}
