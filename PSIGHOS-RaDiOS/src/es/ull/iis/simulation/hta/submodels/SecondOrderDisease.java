/**
 * 
 */
package es.ull.iis.simulation.hta.submodels;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.DiseaseProgression;
import es.ull.iis.simulation.hta.Manifestation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;

/**
 * @author icasrod
 *
 */
public abstract class SecondOrderDisease {
	/** A flag to enable or disable this complication during the simulation run */
	private boolean enable;

	/**
	 * 
	 */
	public SecondOrderDisease() {
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

	/** 
	 * Returns the number of stages used to model this complication
	 * @return the number of stages used to model this complication
	 */
	public abstract int getNManifestations();
	/**
	 * Returns the stages used to model this chronic complication
	 * @return An array containing the stages used to model this chronic complication 
	 */
	public abstract Manifestation[] getManifestations();

	/**
	 * Returns the number of different transitions defined from one manifestation to another
	 * @return the number of different transitions defined from one manifestation to another
	 */
	public abstract int getNTransitions();
	
	/**
	 * Adds the parameters corresponding to the second order uncertainty on the initial proportions for each stage of
	 * the complication
	 * @param secParams Second order parameters repository
	 */
	public void addSecondOrderInitProportion(SecondOrderParamsRepository secParams) {
		for (final Manifestation manif : getManifestations()) {
			if (BasicConfigParams.INIT_PROP.containsKey(manif.name())) {
				secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getInitProbString(manif), "Initial proportion of " + manif.name(), "",
						BasicConfigParams.INIT_PROP.get(manif.name())));
			}			
		}
		
	}
	
	public abstract void addSecondOrderParams(SecondOrderParamsRepository secParams);

	public abstract Disease getInstance(SecondOrderParamsRepository secParams);
	

	/**
	 * The complication instance that should be returned when the complication is disabled.
	 * This instance ensures that no actions are performed.
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public class DisabledDiseaseInstance extends Disease {

		public DisabledDiseaseInstance(SecondOrderDisease secOrder) {
			super(secOrder);
		}

		@Override
		public TreeSet<Manifestation> getInitialStage(Patient pat) {
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
