/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderChronicComplicationSubmodel extends SecondOrderComplicationSubmodel {
	private final DiabetesChronicComplications type;
	/**
	 * 
	 */
	public SecondOrderChronicComplicationSubmodel(final DiabetesChronicComplications type, final EnumSet<DiabetesType> diabetesTypes) {
		super(diabetesTypes);
		this.type = type;
	}

	public DiabetesChronicComplications getComplicationType() {
		return type;
	}
	/** 
	 * Returns the number of stages used to model this complication
	 * @return the number of stages used to model this complication
	 */
	public abstract int getNStages();
	/**
	 * Returns the stages used to model this chronic complication
	 * @return An array containing the stages used to model this chronic complication 
	 */
	public abstract DiabetesComplicationStage[] getStages();

}
