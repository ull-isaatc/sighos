/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.submodels;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

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

	/**
	 * Returns the number of different transitions defined from one stage to another
	 * @return the number of different transitions defined from one stage to another
	 */
	public abstract int getNTransitions();
	
	/**
	 * Adds the parameters corresponding to the second order unvertainty on the initial proportions for each stage of
	 * the complication
	 * @param secParams Second order parameters repository
	 */
	public void addSecondOrderInitProportion(SecondOrderParamsRepository secParams) {
		for (final DiabetesComplicationStage stage : getStages()) {
			if (BasicConfigParams.INIT_PROP.containsKey(stage.name())) {
				secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getInitProbString(stage), "Initial proportion of " + stage.name(), "",
						BasicConfigParams.INIT_PROP.get(stage.name())));
			}			
		}
		
	}
}
