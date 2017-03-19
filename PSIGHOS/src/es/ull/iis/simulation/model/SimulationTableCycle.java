/**
 * 
 */
package es.ull.iis.simulation.model;

import es.ull.iis.util.Cycle;
import es.ull.iis.util.TableCycle;

/**
 * A wrapper class for {@link es.ull.iis.util.TableCycle TableCycle} to be used inside a simulation. 
 * Thus {@link TimeStamp} can be used to define the cycle parameters.
 * @author Iván Castilla Rodríguez
 *
 */
public class SimulationTableCycle implements SimulationCycle {
	/** Inner {@link es.ull.iis.util.TableCycle TableCycle} */ 
	private final TableCycle cycle;
	
	/**
	 * Creates a new cycle which activates according to the timestamps indicated.
	 * @param unit ParallelSimulationEngine time unit 
	 * @param timestamps Set of timestamps to indicate the activations of this cycle
	 */
	public SimulationTableCycle(TimeUnit unit, TimeStamp[] timestamps) {
		cycle = new TableCycle(simulationTime2Double(unit, timestamps));
	}

	/**
	 * Creates a new cycle containing a subcycle which activates according to the timestamps indicated.
	 * @param unit ParallelSimulationEngine time unit 
	 * @param timestamps Set of timestamps to indicate the activations of this cycle
	 * @param subCycle Subcycle contained in this cycle
	 */
	public SimulationTableCycle(TimeUnit unit, TimeStamp[] timestamps, SimulationCycle subCycle) {
		cycle = new TableCycle(simulationTime2Double(unit, timestamps), subCycle.getCycle());
	}

	/**
	 * Converts a set of timestamps to an array of double values. Values are double for backwards
	 * compatibility.
	 * @param unit ParallelSimulationEngine time unit
	 * @param timestamps A set of timestamps
	 * @return an array of double values with the activation times of this cycle expressed in the
	 * indicated <tt>unit</tt>
	 */
	private static double[] simulationTime2Double(TimeUnit unit, TimeStamp[] timestamps) {
		double[] times = new double[timestamps.length];
		for (int i = 0; i < times.length; i++)
			times[i] = unit.convert(timestamps[i]);
		return times;
	}

	@Override
	public Cycle getCycle() {
		return cycle;
	}
}
