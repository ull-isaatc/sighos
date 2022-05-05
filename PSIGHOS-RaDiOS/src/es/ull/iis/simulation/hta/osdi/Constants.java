package es.ull.iis.simulation.hta.osdi;

public interface Constants {
	public static String CONSTANT_DO_NOTHING = "DO_NOTHING";
	public static String CONSTANT_EMPTY_STRING = "";
	public static String CONSTANT_EMPTY_JSON = "{}";
	public static String CONSTANT_DEFAULT_SPACES_SHOW_NODE = "      ";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_3DEC = "%.3f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_6DEC = "%.6f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_9DEC = "%.9f";
	public static String CONSTANT_DOUBLE_FORMAT_STRING_12DEC = "%.12f";
	public static String CONSTANT_DOUBLE_TYPE = "#double";
	public static String CONSTANT_STRING_TYPE = "#string";
	public static String CONSTANT_HASHTAG = "#";
	public static String CONSTANT_NEGATION = "!";
	public static String CONSTANT_UNDEFINED_TYPE = "#string";
	public static String CONSTANT_SPLIT_TYPE = "::";
	public static String CONSTANT_DECISION_NODE = "(*)";
	public static String CONSTANT_SENSITIVITY_100 = "1.0";
	public static String CONSTANT_SPECIFICITY_100 = "1.0";
	public static String CONSTANT_VALUE_ZERO = "0.0";
	public static String CONSTANT_VALUE_ONE = "1.0";
	public static String CONSTANT_DISTRUBUTION_SUFFIX = "Distribution";
	public static int CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_GLOBAL = 0;
	public static int CONSTANT_COST_CALCULATION_STRATEGY_ALTERNATIVE_SPECIFIC = 1;
	
	public static String REGEX_ANYEXPRESION_TYPE = ".*::[#a-zA-Z0-9(),]+";
	public static String REGEX_NUMERICVALUE_DISTRO = "[0-9.,E-]+#[a-zA-Z0-9(),.#]+";
	public static String REGEX_OPERATION_NUMERICVALUE = "[*/+-][0-9.,E-]+";
   public static String REGEX_NUMERICVALUE = "[0-9.,E-]+";	

	// Disease Datasheet properties
   public static String DATASHEET_NATURAL_DEVELOPMENT_LIFE_EXPECTANCY = "naturalDevelopmentLifeExpectancy"; 
   public static String DATASHEET_FOLLOWUP_TREATMENT_STRATEGIES_COSTS = "followUpTreatmentStrategiesCosts"; 
   public static String DATASHEET_PREVALENCE_AT_BITH = "prevalenceAtBith"; 
   public static String DATASHEET_DISCOUNT_RATE_COSTS = "discountRateCosts"; 
   public static String DATASHEET_DISCOUNT_RATE_EFFECTS = "discountRateEffects"; 
   public static String DATASHEET_UTILITY_GENERAL_POPULATION = "utilityGeneralPopulation"; 
   public static String DATASHEET_INTERVENTION_SUMMARY_QALYS = "summaryQalys"; 
   public static String DATASHEET_INTERVENTION_SUMMARY_COSTS = "summaryCosts"; 

	// Data properties that do not belong to the ontology, but are generated on the fly
	public static String CUSTOM_PROPERTY_CUMULATIVE_COST = "#hasCumulativeCost";
	public static String CUSTOM_PROPERTY_COST_TYPE = "#hasCostType";
	public static String CUSTOM_PROPERTY_CUMULATIVE_PROBABILITY = "#hasCumulativeProbability";
	public static String CUSTOM_PROPERTY_PROBABILITY_DISTRIBUTION = "#hasProbability" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_PROBABILITY_MODIFICATION_DISTRIBUTION = "#hasProbabilityModification" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_FREQUENCY_MODIFICATION_DISTRIBUTION = "#hasFrequencyModification" + CONSTANT_DISTRUBUTION_SUFFIX;	
	public static String CUSTOM_PROPERTY_MORTALITY_FACTOR_MODIFICATION_DISTRIBUTION = "#hasMortalityFactorModification" + CONSTANT_DISTRUBUTION_SUFFIX;	
	public static String CUSTOM_PROPERTY_RELATIVE_RISK_MODIFICATION_DISTRIBUTION = "#hasRelativeRiskModification" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_AMOUNT = "#hasAmount";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE = "#hasUtilityValue";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM = "#hasUtilityValueMinimum";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM_WITH_DISCOUNT = "#hasUtilityValueMinimumWithDiscount";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MULTIPLICATIVE = "#hasUtilityValueMultiplicative";
	public static String CUSTOM_PROPERTY_UTILITY_VALUE_MULTIPLICATIVE_WITH_DISCOUNT = "#hasUtilityValueMultiplicativeWithDiscount";
	public static String CUSTOM_PROPERTY_UTILITY_DISTRIBUTION = "#hasUtilityValue" + CONSTANT_DISTRUBUTION_SUFFIX;
	public static String CUSTOM_PROPERTY_UTILITY_KIND = "#hasUtilityKind";
	public static String CUSTOM_PROPERTY_ANNUAL_COST = "#hasAnnualCost";
	public static String CUSTOM_PROPERTY_ONETIME_COST = "#hasOnetimeCost";
	public static String CUSTOM_PROPERTY_LIFETIME_COST = "#hasLifetimeCost";
	
	public static String UTILITY_DEFAULT_CALCULATION_METHOD = "Unknown";
}
