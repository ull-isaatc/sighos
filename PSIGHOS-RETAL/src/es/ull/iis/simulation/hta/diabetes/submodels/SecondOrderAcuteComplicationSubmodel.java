/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderAcuteComplicationSubmodel extends SecondOrderComplicationSubmodel {
	private final DiabetesAcuteComplications type;
	/**
	 * @param diabetesTypes
	 */
	public SecondOrderAcuteComplicationSubmodel(final DiabetesAcuteComplications type, EnumSet<DiabetesType> diabetesTypes) {
		super(diabetesTypes);
		this.type = type;
	}

	public final DiabetesAcuteComplications getComplicationType() {
		return type;
	}
}
