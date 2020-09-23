/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderComplicationSubmodel {
	/** A flag to enable or disable this complication during the simulation run */
	private boolean enable;

	/**
	 * 
	 */
	public SecondOrderComplicationSubmodel() {
		enable = true;
	}

	/**
	 * Disables this complication
	 */
	public void disable() {
		enable = false;
	}

	public boolean isEnabled() {
		return enable;
	}

	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);

	public abstract ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams);
}
