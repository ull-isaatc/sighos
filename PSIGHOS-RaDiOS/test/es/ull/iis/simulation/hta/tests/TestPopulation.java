/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.calculator.ConstantDeathSubmodel;
import es.ull.iis.simulation.hta.progression.calculator.TimeToEventCalculator;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPopulation extends StdPopulation {
	final private TimeToEventCalculator death;
	/**
	 * 
	 * @param disease
	 */
	public TestPopulation(HTAModel model, Disease disease) throws MalformedSimulationModelException {
		super(model, "TEST_POP", "Test population", disease);
		death = new ConstantDeathSubmodel(BasicConfigParams.DEF_MAX_AGE - BasicConfigParams.DEF_MIN_AGE);
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("DiscreteConstantVariate", getCommonRandomNumber(), 0);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("DiscreteConstantVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("DiscreteConstantVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		return RandomVariateFactory.getInstance("ConstantVariate", BasicConfigParams.DEF_MIN_AGE);
	}

	@Override
	public void createParameters() {
		addUsedParameter(StandardParameter.POPULATION_BASE_UTILITY, "", "Assumption", BasicConfigParams.DEF_U_GENERAL_POP);
	}

	@Override
	public int getMinAge() {
		return BasicConfigParams.DEF_MIN_AGE;
	}

	@Override
	public TimeToEventCalculator getDeathCharacterization() {
		return death;
	}
}
