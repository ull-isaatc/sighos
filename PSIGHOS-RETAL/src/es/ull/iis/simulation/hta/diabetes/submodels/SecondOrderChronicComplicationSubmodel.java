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
	/** The type of the complication among those defined in {@link DiabetesChronicComplications} */
	private final DiabetesChronicComplications type;
	
	/**
	 * Creates the second order definition of a chronic complication submodel 
	 * @param type The type of the complication among those defined in {@link DiabetesChronicComplications}
	 * @param diabetesTypes Diabetes types that this submodel can be used for
	 */
	public SecondOrderChronicComplicationSubmodel(final DiabetesChronicComplications type, final EnumSet<DiabetesType> diabetesTypes) {
		super(diabetesTypes);
		this.type = type;
	}

	/**
	 * Returns the type of the complication among those defined in {@link DiabetesChronicComplications}
	 * @return the type of the complication 
	 */
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
	 * Adds the parameters corresponding to the second order uncertainty on the initial proportions for each stage of
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
