/**
 * 
 */
package es.ull.isaatc.simulation.model;

import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ModelPeriodicCycle implements	ModelCycle {
	private final PeriodicCycle cycle;
	
	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public ModelPeriodicCycle(Model model, Time startTs, ModelTimeFunction period, Time endTs) {
		cycle = new PeriodicCycle(model.simulationTime2Double(startTs), period.getFunction(), model.simulationTime2Double(endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public ModelPeriodicCycle(Model model, Time startTs, ModelTimeFunction period,
			int iterations) {
		cycle = new PeriodicCycle(model.simulationTime2Double(startTs), period.getFunction(), iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public ModelPeriodicCycle(Model model, Time startTs, ModelTimeFunction period,
			Time endTs, ModelCycle subCycle) {
		cycle = new PeriodicCycle(model.simulationTime2Double(startTs), period.getFunction(), model.simulationTime2Double(endTs), subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public ModelPeriodicCycle(Model model, Time startTs, ModelTimeFunction period,
			int iterations, ModelCycle subCycle) {
		cycle = new PeriodicCycle(model.simulationTime2Double(startTs), period.getFunction(), iterations, subCycle.getCycle());
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 */
	public ModelPeriodicCycle(Model model, double startTs, ModelTimeFunction period, double endTs) {
		this(model, new Time(model.getUnit(), startTs), period, new Time(model.getUnit(), endTs));
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 */
	public ModelPeriodicCycle(Model model, double startTs, ModelTimeFunction period,
			int iterations) {
		this(model, new Time(model.getUnit(), startTs), period, iterations);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param endTs
	 * @param subCycle
	 */
	public ModelPeriodicCycle(Model model, double startTs, ModelTimeFunction period,
			double endTs, ModelCycle subCycle) {
		this(model, new Time(model.getUnit(), startTs), period, new Time(model.getUnit(), endTs), subCycle);
	}

	/**
	 * @param startTs
	 * @param period
	 * @param iterations
	 * @param subCycle
	 */
	public ModelPeriodicCycle(Model model, double startTs, ModelTimeFunction period,
			int iterations, ModelCycle subCycle) {
		this(model, new Time(model.getUnit(), startTs), period, iterations, subCycle);
	}

	/**
	 * @return the cycle
	 */
	public Cycle getCycle() {
		return cycle;
	}

}
