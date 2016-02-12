/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.core.SimulationCycle;

/**
 * @author icasrod
 *
 */
public class Screening implements Intervention {
	private final SimulationCycle screeningCycle;
	private final double specificity;
	private final double sensitivity;

	/**
	 * 
	 * @param screeningCycle
	 * @param specificity
	 * @param sensitivity
	 */
	public Screening(SimulationCycle screeningCycle, double specificity, double sensitivity) {
		this.screeningCycle = screeningCycle;
		this.specificity = specificity;
		this.sensitivity = sensitivity;
	}

	/**
	 * @return the screeningCycle
	 */
	public SimulationCycle getScreeningCycle() {
		return screeningCycle;
	}

	/**
	 * @return the specificity
	 */
	public double getSpecificity() {
		return specificity;
	}

	/**
	 * @return the sensitivity
	 */
	public double getSensitivity() {
		return sensitivity;
	}

}
