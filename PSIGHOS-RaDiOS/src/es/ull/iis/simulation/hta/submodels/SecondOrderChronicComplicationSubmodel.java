/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.ChronicComplication;
import es.ull.iis.simulation.hta.ComplicationStage;
import es.ull.iis.simulation.hta.DiseaseProgression;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderChronicComplicationSubmodel extends SecondOrderComplicationSubmodel {
	/** The comp of the complication among those defined in {@link ChronicComplication} */
	private final ChronicComplication comp;
	
	/**
	 * Creates the second order definition of a chronic complication submodel 
	 * @param comp The comp of the complication among those defined in {@link ChronicComplication}
	 * @param diabetesTypes Diabetes types that this submodel can be used for
	 */
	public SecondOrderChronicComplicationSubmodel(final ChronicComplication comp) {
		super();
		this.comp = comp;
	}

	public final ChronicComplication getComplication() {
		return comp;
	}

	/**
	 * Returns the comp of the complication among those defined in {@link ChronicComplication}
	 * @return the comp of the complication 
	 */
	public ChronicComplication getComplicationType() {
		return comp;
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
	public abstract ComplicationStage[] getStages();

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
		for (final ComplicationStage stage : getStages()) {
			if (BasicConfigParams.INIT_PROP.containsKey(stage.name())) {
				secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getInitProbString(stage), "Initial proportion of " + stage.name(), "",
						BasicConfigParams.INIT_PROP.get(stage.name())));
			}			
		}
		
	}

	/**
	 * The complication instance that should be returned when the complication is disabled.
	 * This instance ensures that no actions are performed.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DisabledChronicComplicationInstance extends ChronicComplicationSubmodel {

		public DisabledChronicComplicationInstance(SecondOrderChronicComplicationSubmodel secOrder) {
			super(secOrder);
		}

		@Override
		public TreeSet<ComplicationStage> getInitialStage(Patient pat) {
			return new TreeSet<>();
		}
		@Override
		public DiseaseProgression getProgression(Patient pat) {
			return new DiseaseProgression();
		}

		@Override
		public double getAnnualCostWithinPeriod(Patient pat, double initAge, double endAge) {
			return 0;
		}

		@Override
		public double getDisutility(Patient pat, DisutilityCombinationMethod method) {
			return 0;
		}
		
	}
}
