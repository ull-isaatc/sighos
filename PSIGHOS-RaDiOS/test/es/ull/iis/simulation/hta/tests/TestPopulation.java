/**
 * 
 */
package es.ull.iis.simulation.hta.tests;

import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.ConstantNatureParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.populations.Population;
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
	final public static String ATTRIBUTE_LDL = "LDL";
	final public static String ATTRIBUTE_HDL = "HDL";
	private double ldl = 85.0;
	private double hdl = 55.0;
	private int sex = 0;
	private int age = Population.DEF_MIN_AGE;
	
	/**
	 * 
	 * @param disease
	 */
	public TestPopulation(HTAModel model, Disease disease) throws MalformedSimulationModelException {
		super(model, "TEST_POP", "Test population", disease);
	}

	@Override
	protected DiscreteRandomVariate getSexVariate(Patient pat) {
		return RandomVariateFactory.getDiscreteRandomVariateInstance("DiscreteConstantVariate", getCommonRandomNumber(), sex);
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
		return RandomVariateFactory.getInstance("ConstantVariate", age);
	}

	@Override
	public void createParameters() {
		model.addParameter(new ConstantNatureParameter(getModel(), ATTRIBUTE_HDL, "High Density Lipoprotein", "", ParameterType.ATTRIBUTE, hdl));
		model.addParameter(new ConstantNatureParameter(getModel(), ATTRIBUTE_LDL, "Low Density Lipoprotein", "", ParameterType.ATTRIBUTE, ldl));

		addUsedParameter(StandardParameter.POPULATION_BASE_UTILITY, "", "Assumption", Population.DEF_U_GENERAL_POP);
	}

	@Override
	public int getMinAge() {
		return Math.min(age, Population.DEF_MIN_AGE);
	}

	@Override
	public TimeToEventCalculator initializeDeathCharacterization() {
		return new ConstantDeathSubmodel(Population.DEF_MAX_AGE - Population.DEF_MIN_AGE);
	}

	public double getLDL() {
		return ldl;
	}

	public void setLDL(double ldl) {
		this.ldl = ldl;
	}

	public double getHDL() {
		return hdl;
	}

	public void setHDL(double hdl) {
		this.hdl = hdl;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
