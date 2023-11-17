/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RiskParamDescriptions;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class PBDPopulation extends StdPopulation {
	private static final double BIRTH_PREVALENCE = 1.47884E-05;
	private final boolean allAffected;
	/**
	 * @param disease
	 */
	public PBDPopulation(SecondOrderParamsRepository secParams, Disease disease, boolean allAffected) throws MalformedSimulationModelException {
		super(secParams, "PBD_POP", "Population for PBD", disease);
		this.allAffected = allAffected;
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.5);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(Patient pat) {
		final double birthPrev = allAffected ? 1.0 : RiskParamDescriptions.BIRTH_PREVALENCE.getValue(getRepository(), disease, pat);
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), birthPrev);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, this, "Utility for Spanish general population", 0.8861);
		if (!allAffected)
			RiskParamDescriptions.BIRTH_PREVALENCE.addParameter(secParams, disease, "", 
				BIRTH_PREVALENCE, RandomVariateFactory.getInstance("BetaVariate", 8, 540955));
	}

	@Override
	public int getMinAge() {
		return 0;
	}
}
