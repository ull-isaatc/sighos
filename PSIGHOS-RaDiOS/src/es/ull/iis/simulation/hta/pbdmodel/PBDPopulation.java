/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.params.DefaultProbabilitySecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
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
	public PBDPopulation(SecondOrderParamsRepository secParams, Disease disease, boolean allAffected) {
		super(secParams, disease);
		this.allAffected = allAffected;
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.5);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
		final double birthPrev = allAffected ? 1.0 : DefaultProbabilitySecondOrderParam.BIRTH_PREVALENCE.getValue(secParams, disease, simul);
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), birthPrev);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

	@Override
	public void registerSecondOrderParameters() {
		secParams.addBaseUtilityParam("Base utility for the general population of PBD", "Utility for Spanish general population", 0.8861, RandomVariateFactory.getInstance("ConstantVariate", 0.8861));
		if (!allAffected)
			DefaultProbabilitySecondOrderParam.BIRTH_PREVALENCE.addParameter(secParams, disease, disease, "", 
				BIRTH_PREVALENCE, RandomVariateFactory.getInstance("BetaVariate", 8, 540955));
	}

	@Override
	public int getMinAge() {
		return 0;
	}
}
