/**
 * 
 */
package es.ull.iis.simulation.hta.simpletest;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.UtilityParamDescriptions;
import es.ull.iis.simulation.hta.populations.StdPopulation;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.EmpiricalSpainDeathSubmodel;
import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestPopulation extends StdPopulation {

	/**
	 * @param disease
	 */
	public TestPopulation(SecondOrderParamsRepository secParams, Disease disease) throws MalformedSimulationModelException {
		super(secParams, "TEST_POP", "Test population", disease);
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 0.5);
	}

	@Override
	protected DiscreteRandomVariate getDiseaseVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected DiscreteRandomVariate getDiagnosedVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("BernoulliVariate", getCommonRandomNumber(), 1.0);
	}

	@Override
	protected RandomVariate getBaselineAgeVariate(Patient pat) {
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
	}

	@Override
	public void registerSecondOrderParameters(SecondOrderParamsRepository secParams) {
		UtilityParamDescriptions.BASE_UTILITY.addParameter(secParams, this, "Assumption", BasicConfigParams.DEF_U_GENERAL_POP);
	}

	@Override
	public int getMinAge() {
		return 0;
	}

	@Override
	public DiseaseProgression getDeathCharacterization() {
		return new EmpiricalSpainDeathSubmodel(getRepository(), disease);
	}
}
