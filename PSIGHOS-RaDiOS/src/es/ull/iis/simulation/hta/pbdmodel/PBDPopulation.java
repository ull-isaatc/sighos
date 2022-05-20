/**
 * 
 */
package es.ull.iis.simulation.hta.pbdmodel;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
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
	private static final String STR_BIRTH_PREV = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "BIRTH_PREVALENCE";
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
		final double birthPrev = allAffected ? 1.0 : secParams.getProbParam(STR_BIRTH_PREV, simul);
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
		if (!allAffected)
			secParams.addProbParam(
				new SecondOrderParam(secParams, STR_BIRTH_PREV, "Birth prevalence", "", 
				BIRTH_PREVALENCE, RandomVariateFactory.getInstance("BetaVariate", 8, 540955)));
	}

	@Override
	public int getMinAge() {
		return 0;
	}
}
