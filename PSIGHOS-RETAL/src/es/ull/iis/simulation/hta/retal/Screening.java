/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import es.ull.iis.simulation.hta.retal.params.ScreeningParam;
import es.ull.iis.simulation.model.SimulationCycle;

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
	public double getSpecificity(RetalPatient pat) {
		return param.getSpecificity(pat);
	}

	/**
	 * @return the sensitivity
	 */
	public double getSensitivity(RetalPatient pat) {
		return param.getSensitivity(pat);
	}

	public boolean isAttending(RetalPatient pat) {
		return param.isAttending(pat);
	}
	
	@Override
	public int getId() {
		return 1;
	}

	@Override
	public String getDescription() {
		return "Screening";
	}

	@Override
	public String getShortName() {
		return "SCR";
	}

}
