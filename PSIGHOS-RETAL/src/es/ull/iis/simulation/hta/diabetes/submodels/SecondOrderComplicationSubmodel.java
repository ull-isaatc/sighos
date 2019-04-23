/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderComplicationSubmodel {
	/** A flag to enable or disable this complication during the simulation run */
	private boolean enable;
	private final EnumSet<DiabetesType> diabetesTypes;

	/**
	 * 
	 */
	public SecondOrderComplicationSubmodel(final EnumSet<DiabetesType> diabetesTypes) {
		enable = true;
		this.diabetesTypes = diabetesTypes;
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
	
	public EnumSet<DiabetesType> getDiabetesTypes() {
		return diabetesTypes;
	}

	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);

	public abstract ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams);
}
