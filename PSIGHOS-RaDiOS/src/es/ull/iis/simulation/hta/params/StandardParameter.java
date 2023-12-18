package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.NamedAndDescribed;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * The standard parameters used by a model component.
 */
public enum StandardParameter {
	ANNUAL_COST(0.0, "Annual cost of a model component", ParameterType.COST, "AC_"),
	ONE_TIME_COST(0.0, "One-time cost of a model component", ParameterType.COST, "OC_"),
    BIRTH_PREVALENCE(0.0, "Birth prevalence of a disease", ParameterType.RISK, "BIRTH_PREV_"),
    INCIDENCE(0.0, "Incidence of a model component", ParameterType.RISK, "INC_"),
    PREVALENCE(0.0, "Prevalence of a model component", ParameterType.RISK, "PREV_"),
	ONSET_COST(0.0, "Cost applied to a model component when it appears", ParameterType.COST, "TC_"),
	DISEASE_DIAGNOSIS_COST(0.0, "Diagnosis cost of a model component", ParameterType.COST, "C_DIAG_"),
	ANNUAL_DISUTILITY(0.0, "Annual disutility of a model component", ParameterType.DISUTILITY, "DU_"),
	FOLLOW_UP_COST(0.0, "Annual follow up cost of a model component", ParameterType.COST, "C_FOLLOW_"),
	ONSET_DISUTILITY(0.0, "Disutility to be applied on onset of a model component", ParameterType.DISUTILITY, "TDU_"),
	TREATMENT_COST(0.0, "Annual treatment cost of a model component", ParameterType.COST, "C_TREAT_"),
	DISEASE_PROGRESSION_END_AGE(0.0, "Age at which the disease progression ends", ParameterType.OTHER),
	DISEASE_PROGRESSION_ONSET_AGE(0.0, "Age at which the disease progression starts", ParameterType.OTHER),
	DISEASE_PROGRESSION_INITIAL_PROPORTION(0.0, "Initial proportion of patients with this disease progression", ParameterType.RISK),
	DISEASE_PROGRESSION_RISK_OF_DEATH(0.0, "Risk of death due to this disease progression", ParameterType.RISK),
	DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS(0.0, "Probability of diagnosis upon onset of this disease progression", ParameterType.RISK),
    POPULATION_BASE_UTILITY(1.0, "Base utility of a population", ParameterType.UTILITY, "U_"),
	POPULATION_MAX_AGE(BasicConfigParams.DEF_MAX_AGE, "Maximum age of the population", ParameterType.OTHER),
	POPULATION_MIN_AGE(BasicConfigParams.DEF_MIN_AGE, "Minimum age of the population", ParameterType.OTHER),
	TIME_TO_EVENT(Double.NaN, "Time to event", ParameterType.RISK, "TTE_"),
	INCREASED_MORTALITY_RATE(1.0, "Increased mortality rate", ParameterType.OTHER),
	LIFE_EXPECTANCY_REDUCTION(0.0, "Life expectancy reduction", ParameterType.OTHER),
	PROBABILITY(0.0, "Probability", ParameterType.RISK, "P_"),
	PROPORTION(0.0, "Proportion", ParameterType.RISK, "P_"),
	RELATIVE_RISK(1.0, "Relative risk", ParameterType.RISK, "RR_"),
    SPECIFICITY(1.0, "Specificity", ParameterType.RISK, "SPEC_"),
    SENSITIVITY(1.0, "Sensitivity", ParameterType.RISK, "SENS_"),;

	private final double defaultValue;
	private final String defaultDescription;
	private final ParameterType type;
	private final String defaultPrefix;

	private StandardParameter(double defaultValue, String defaultDescription, ParameterType type, String defaultPrefix) {
		this.defaultValue = defaultValue;
		this.defaultDescription = defaultDescription;
		this.type = type;
		this.defaultPrefix = defaultPrefix;
	}

	private StandardParameter(double defaultValue, String defaultDescription, ParameterType type) {
		this(defaultValue, defaultDescription, type, "");
	}

	/**
	 * @return the defaultDescription
	 */
	public String getDefaultDescription() {
		return defaultDescription;
	}

	/**
	 * @return the defaultValue
	 */
	public double getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the type
	 */
	public ParameterType getType() {
		return type;
	}

	public String createName(String name) {
		return defaultPrefix + name;
	}

	public String createName(Named component) {
		return createName(component.name());
	}

    public boolean addParameter(HTAModel model, String name, String description, String source, int year, double detValue) {
        return model.addParameter(new ConstantNatureParameter(model, name, description, source, year, type, detValue));
    }

    public boolean addParameter(HTAModel model, String name, String description, String source, int year, double detValue, RandomVariate rnd) {
        return model.addParameter(new SecondOrderNatureParameter(model, name, description, source, year, type, detValue, rnd));
    }

    public boolean addParameter(HTAModel model, String name, String description, String source, int year, RandomVariate rnd) {
        return model.addParameter(new FirstOrderNatureParameter(model, name, description, source, year, type, rnd));
    }

    public boolean addParameter(HTAModel model, String name, String description, String source, double detValue) {
        return model.addParameter(new ConstantNatureParameter(model, name, description, source, type, detValue));
    }

    public boolean addParameter(HTAModel model, String name, String description, String source, double detValue, RandomVariate rnd) {
        return model.addParameter(new SecondOrderNatureParameter(model, name, description, source, HTAModel.getStudyYear(), type, detValue, rnd));
    }

    public boolean addParameter(HTAModel model, String name, String description, String source, RandomVariate rnd) {
        return model.addParameter(new FirstOrderNatureParameter(model, name, description, source, HTAModel.getStudyYear(), type, rnd));
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, int year, double detValue) {
        return this.addParameter(model, createName(instance), instance.getDescription(), source, year, detValue);
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, int year, double detValue, RandomVariate rnd) {
        return this.addParameter(model, createName(instance), instance.getDescription(), source, year, detValue, rnd);
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, int year, RandomVariate rnd) {
		return this.addParameter(model, createName(instance), instance.getDescription(), source, year, rnd);
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, double detValue) {
		return this.addParameter(model, createName(instance), instance.getDescription(), source, detValue);
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, double detValue, RandomVariate rnd) {
		return this.addParameter(model, createName(instance), instance.getDescription(), source, detValue, rnd);
    }

    public boolean addParameter(HTAModel model, NamedAndDescribed instance, String source, RandomVariate rnd) {
		return this.addParameter(model, createName(instance), instance.getDescription(), source, rnd);
    }

    /**
     * Creates a Gamma distribution to add uncertainty to a deterministic cost. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
     * parameters to adjust the uncertainty
     * @param detCost Deterministic cost
     * @return a Gamma random distribution that represents the uncertainty around a cost
     */
    public static RandomVariate getRandomVariateForCost(double detCost) {
    	if (detCost == 0.0) {
    		return RandomVariateFactory.getInstance("ConstantVariate", detCost);
    	}
    	final double costVariance2 = BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST * BasicConfigParams.DEF_SECOND_ORDER_VARIATION.COST;
    	final double invCostVariance2 = 1 / costVariance2;
    	return RandomVariateFactory.getInstance("GammaVariate", invCostVariance2, costVariance2 * detCost);
    }

    /**
     * Creates a uniform distribution to add uncertainty to a deterministic probability. Uses the {@link BasicConfigParams#DEF_SECOND_ORDER_VARIATION} 
     * parameters to adjust the uncertainty
     * @param detProb Deterministic probability
     * @return a uniform distribution that represents the uncertainty around a probability parameter
     */
    public static RandomVariate getRandomVariateForProbability(double detProb) {
    	if (detProb == 0.0) {
    		return RandomVariateFactory.getInstance("ConstantVariate", detProb);
    	}
    	final double instRate = -Math.log(1 - detProb);
    	return RandomVariateFactory.getInstance("UniformVariate", 1 - Math.exp(-instRate * (1 - BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY)), 1 - Math.exp(-instRate * (1 + BasicConfigParams.DEF_SECOND_ORDER_VARIATION.PROBABILITY)));
    }
}