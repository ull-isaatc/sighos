/**
 * 
 */
package es.ull.isaatc.simulation.common;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.TableCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelTableCycle implements ModelCycle {
	private final TableCycle cycle;
	
	/**
	 * @param timestamps
	 */
	public ModelTableCycle(TimeUnit unit, Time[] timestamps) {
		cycle = new TableCycle(simulationTime2Double(unit, timestamps));
	}

	/**
	 * @param timestamps
	 * @param subCycle
	 */
	public ModelTableCycle(TimeUnit unit, Time[] timestamps, ModelCycle subCycle) {
		cycle = new TableCycle(simulationTime2Double(unit, timestamps), (Cycle)subCycle);
	}

	private static double[] simulationTime2Double(TimeUnit unit, Time[] timestamps) {
		double[] times = new double[timestamps.length];
		for (int i = 0; i < times.length; i++)
			times[i] = unit.time2Double(timestamps[i]);
		return times;
	}

	public Cycle getCycle() {
		return cycle;
	}
}
