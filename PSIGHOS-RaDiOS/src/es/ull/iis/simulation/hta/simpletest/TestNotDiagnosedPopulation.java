/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
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
public class TestNotDiagnosedPopulation extends StdPopulation {
	private static final double BIRTH_PREVALENCE = 0.1;

	/**
	 * @param disease
	 */
	public TestNotDiagnosedPopulation(SecondOrderParamsRepository secParams, Disease disease) {
		super(secParams, "TESTPOP", "Test undiagnosed population", disease);
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.5);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), BIRTH_PREVALENCE);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(DiseaseProgressionSimulation simul) {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

	@Override
	public void registerSecondOrderParameters() {
	}

	@Override
	public int getMinAge() {
		return 0;
	}
}
