/**
 * 
 */
package es.ull.iis.simulation.hta.radios;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.params.SecondOrderParam;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.radios.wrappers.ProbabilityDistribution;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author David Prieto González
 *
 */
public class RadiosPopulation extends StdPopulation {
	private final static double GENERAL_POPULATION_UTILITY = 0.8861;	
	private ProbabilityDistribution birthPrevalence = null;
	private static final String STR_BIRTH_PREV = SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + "BIRTH_PREVALENCE";
	private final boolean allAffected;

	/**
	 * @param disease
	 */
	public RadiosPopulation(SecondOrderParamsRepository secParams, Disease disease, ProbabilityDistribution birthPrevalence, boolean allAffected) {
		super(secParams, disease);
		this.birthPrevalence = birthPrevalence;
		this.allAffected = allAffected;
	}
	
	@Override
	protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.5);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), allAffected ? 1.0 : secParams.getProbParam(STR_BIRTH_PREV, simul));
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
		if (!allAffected && birthPrevalence != null) {
			secParams.addProbParam(new SecondOrderParam(secParams, STR_BIRTH_PREV, "Birth prevalence", "", birthPrevalence.getDeterministicValue(), birthPrevalence.getProbabilisticValue()));
		}
		secParams.addBaseUtilityParam("Base utility", "", GENERAL_POPULATION_UTILITY, RandomVariateFactory.getInstance("ContantVariate", GENERAL_POPULATION_UTILITY));
	}

	@Override
	public int getMinAge() {
		return 0;
	}
}
