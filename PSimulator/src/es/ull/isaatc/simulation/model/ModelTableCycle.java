/**
 * 
 */
package es.ull.isaatc.simulation.model;

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
	public ModelTableCycle(Model model, Time[] timestamps) {
		cycle = new TableCycle(simulationTime2Double(model, timestamps));
	}

	/**
	 * @param timestamps
	 * @param subCycle
	 */
	public ModelTableCycle(Model model, Time[] timestamps, ModelCycle subCycle) {
		cycle = new TableCycle(simulationTime2Double(model, timestamps), (Cycle)subCycle);
	}

	private static double[] simulationTime2Double(Model model, Time[] timestamps) {
		double[] times = new double[timestamps.length];
		for (int i = 0; i < times.length; i++)
			times[i] = model.simulationTime2Double(timestamps[i]);
		return times;
	}

	public Cycle getCycle() {
		return cycle;
	}
}
