package es.ull.iis.simulation.hta.params;

import es.ull.iis.simulation.hta.params.Parameter.ParameterType;

/**
 * The standard parameters used by a model component.
 */
public enum StandardParameter implements ParameterTemplate {
	ANNUAL_COST(0.0, "Annual cost of a model component", ParameterType.COST, "AC_"),
	ANNUAL_DISUTILITY(0.0, "Annual disutility of a model component", ParameterType.DISUTILITY, "DU_"),
	ANNUAL_UTILITY(1.0, "Annual utility of a model component", ParameterType.UTILITY, "U_"),
    BIRTH_PREVALENCE(1.0, "Birth prevalence of a disease", ParameterType.RISK, "BIRTH_PREV_"),
	DISEASE_DIAGNOSIS_COST(0.0, "Diagnosis cost of a model component", ParameterType.COST, "C_DIAG_"),
	DISEASE_PROGRESSION_DURATION(BasicConfigParams.DEF_MAX_AGE - BasicConfigParams.DEF_MIN_AGE, "Duration of the disease progression (in years)", ParameterType.OTHER),
	DISEASE_PROGRESSION_END_AGE(BasicConfigParams.DEF_MAX_AGE, "Age at which the disease progression ends", ParameterType.OTHER),
	DISEASE_PROGRESSION_INITIAL_PROPORTION(0.0, "Initial proportion of patients with this disease progression", ParameterType.RISK, "P_INIT_"),
	DISEASE_PROGRESSION_ONSET_AGE(0.0, "Age at which the disease progression starts", ParameterType.OTHER),
	DISEASE_PROGRESSION_PROBABILITY_OF_DIAGNOSIS(0.0, "Probability of diagnosis upon onset of this disease progression", ParameterType.RISK, "P_DIAG_"),
	DISEASE_PROGRESSION_RISK_OF_DEATH(0.0, "Risk of death due to this disease progression", ParameterType.RISK, "P_DEATH_"),
	FOLLOW_UP_COST(0.0, "Annual follow up cost of a model component", ParameterType.COST, "C_FOLLOW_"),
    INCIDENCE(1.0, "Incidence of a model component", ParameterType.RISK, "INC_"),
	INCIDENCE_RATE_RATIO(1.0, "Incidence rate ratio", ParameterType.RISK, "IRR_"),
	INCREASED_MORTALITY_RATE(1.0, "Increased mortality rate", ParameterType.OTHER),
	LIFE_EXPECTANCY_REDUCTION(0.0, "Life expectancy reduction", ParameterType.OTHER),
	ONE_TIME_COST(0.0, "One-time cost of a model component", ParameterType.COST, "OC_"),
	ONSET_COST(0.0, "Cost applied to a model component when it appears", ParameterType.COST, "TC_"),
	ONSET_DISUTILITY(0.0, "Disutility to be applied on onset of a model component", ParameterType.DISUTILITY, "TDU_"),
	ONSET_UTILITY(1.0, "Utility to be applied on onset of a model component", ParameterType.DISUTILITY, "TU_"),
    POPULATION_BASE_UTILITY(1.0, "Base utility of a population", ParameterType.UTILITY, "U_"),
	POPULATION_MAX_AGE(BasicConfigParams.DEF_MAX_AGE, "Maximum age of the population", ParameterType.OTHER),
	POPULATION_MIN_AGE(BasicConfigParams.DEF_MIN_AGE, "Minimum age of the population", ParameterType.OTHER),
    PREVALENCE(1.0, "Prevalence of a model component", ParameterType.RISK, "PREV_"),
	PROBABILITY(0.0, "Probability", ParameterType.RISK, "P_"),
	PROPORTION(0.0, "Proportion", ParameterType.RISK, "P_"),
	RATE(0.0, "Rate", ParameterType.RISK, "RATE_"),
	RELATIVE_RISK(1.0, "Relative risk", ParameterType.RISK, "RR_"),
	RESOURCE_USAGE(1.0, "Resource usage", ParameterType.OTHER, "USAGE_"),
    SENSITIVITY(1.0, "Sensitivity", ParameterType.RISK, "SENS_"),
    SPECIFICITY(1.0, "Specificity", ParameterType.RISK, "SPEC_"),
	TIME_TO_EVENT(Double.NaN, "Time to event", ParameterType.RISK, "TTE_"),
	TREATMENT_COST(0.0, "Annual treatment cost of a model component", ParameterType.COST, "C_TREAT_"),
	UNIT_COST(0.0, "Unit cost of a model component", ParameterType.COST, "UC_");

	private final double defaultValue;
	private final String defaultDescription;
	private final ParameterType type;
	private final String prefix;

	private StandardParameter(double defaultValue, String defaultDescription, ParameterType type, String prefix) {
		this.defaultValue = defaultValue;
		this.defaultDescription = defaultDescription;
		this.type = type;
		this.prefix = prefix;
	}

	private StandardParameter(double defaultValue, String defaultDescription, ParameterType type) {
		this(defaultValue, defaultDescription, type, "");
	}

	@Override
	public String getDefaultDescription() {
		return defaultDescription;
	}

	@Override
	public double getDefaultValue() {
		return defaultValue;
	}

	@Override
	public ParameterType getType() {
		return type;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}
}