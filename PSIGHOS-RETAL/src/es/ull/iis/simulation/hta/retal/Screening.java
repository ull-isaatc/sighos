/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.retal.params.ScreeningParam;

/**
 * @author icasrod
 *
 */
public class Screening implements Intervention {
	private final SimulationCycle screeningCycle;
	private final ScreeningParam param;

	/**
	 * 
	 * @param screeningCycle
	 * @param specificity
	 * @param sensitivity
	 */
	public Screening(SimulationCycle screeningCycle, ScreeningParam param) {
		this.screeningCycle = screeningCycle;
		this.param = param;
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
	public double getSpecificity(Patient pat) {
		return param.getSpecificity(pat);
	}

	/**
	 * @return the sensitivity
	 */
	public double getSensitivity(Patient pat) {
		return param.getSensitivity(pat);
	}

	public boolean isAttending(Patient pat) {
		return param.isAttending(pat);
	}
	
	@Override
	public int getId() {
		return 1;
	}

}
